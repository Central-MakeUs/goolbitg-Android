package com.project.data.remote.socket

import android.util.Log
import com.google.gson.Gson
import com.project.data.BuildConfig
import com.project.data.remote.common.BaseUrl
import com.project.data.remote.response.buyornot.ChatMessageRes
import com.project.domain.model.buyornot.ChatMessageModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.OkHttpClient
import okhttp3.Protocol
import org.hildan.krossbow.stomp.StompClient
import org.hildan.krossbow.stomp.StompSession
import org.hildan.krossbow.stomp.config.HeartBeat
import org.hildan.krossbow.stomp.sendText
import org.hildan.krossbow.stomp.subscribeText
import org.hildan.krossbow.websocket.okhttp.OkHttpWebSocketClient
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.seconds

/**
 * STOMP-over-WebSocket 채팅 클라이언트.
 *
 * - connect URL: [BaseUrl.BASE_WS_URL]
 * - subscribe destination: `/topic/chat/{buyOrNotId}`
 * - send destination: `/app/chat/{buyOrNotId}`
 *
 * 단일 세션을 재사용하며, ViewModel 의 라이프사이클이 끝나면 [disconnect] 로 정리한다.
 */
@Singleton
class StompChatClient @Inject constructor() {

    /**
     * Cloudflare 가 ALPN h2 를 협상해 HTTP/2 응답으로 내려보내면서 WebSocket Upgrade 가
     * 거부되는 문제를 회피하기 위해 OkHttpClient 자체를 HTTP/1.1 만 사용하도록 강제한다.
     * (OkHttp 의 WebSocket 은 RFC 8441(WebSocket over HTTP/2) 을 지원하지 않음)
     *
     * pingInterval — Cloudflare 등 중간 프록시가 idle 연결을 닫아 [java.io.EOFException]
     * 으로 떨어지는 것을 막기 위해 20초마다 WebSocket ping 프레임을 자동 송신한다.
     */
    private val httpClient = OkHttpClient.Builder()
        .protocols(listOf(Protocol.HTTP_1_1))
        .pingInterval(20, TimeUnit.SECONDS)
        .build()

    private val stompClient = StompClient(OkHttpWebSocketClient(httpClient)) {
        // STOMP 자체 heart-beat 도 같이 켜둔다. 서버가 지원하지 않으면 ping interval 이 fallback.
        heartBeat = HeartBeat(
            minSendPeriod = 10.seconds,
            expectedPeriod = 10.seconds
        )
    }
    private val gson = Gson()
    private val mutex = Mutex()

    private var session: StompSession? = null

    /**
     * 연결 상태 — UI 에서 디버그 표시 등에 활용 가능.
     * State 변화: [State.Idle] → [State.Connecting] → [State.Connected] / [State.Failed]
     *           → [State.Disconnected] (외부 close 시) / [State.Idle] (명시적 disconnect)
     */
    private val _connectionState = MutableStateFlow<State>(State.Idle)
    val connectionState: StateFlow<State> = _connectionState.asStateFlow()

    sealed class State {
        object Idle : State()
        object Connecting : State()
        data class Connected(val url: String) : State()
        data class Failed(val message: String?) : State()
        object Disconnected : State()
    }

    /** 연결 (이미 살아있으면 no-op). 재연결이 필요한 경우 [disconnect] 후 재호출. */
    suspend fun connect() {
        mutex.withLock {
            if (session != null) {
                Log.d(TAG, "connect(): already connected, reusing session")
                return
            }
            // NetworkModule 의 REST endpoint 분기와 동일하게 debug 빌드에서는 dev 서버 사용
            val url = if (BuildConfig.DEBUG) BaseUrl.BASE_WS_URL_DEV else BaseUrl.BASE_WS_URL
            Log.d(TAG, "connect(): connecting to $url")
            _connectionState.value = State.Connecting
            try {
                session = stompClient.connect(url)
                Log.d(TAG, "connect(): connected ✓")
                _connectionState.value = State.Connected(url)
            } catch (t: Throwable) {
                Log.e(TAG, "connect(): failed", t)
                _connectionState.value = State.Failed(t.message)
                throw t
            }
        }
    }

    /**
     * 특정 게시물의 채팅 토픽을 구독한다. 새 메시지가 들어올 때마다 [ChatMessageModel] 로 emit.
     * 들어오는 모든 raw JSON 도 logcat 으로 출력 (TAG: [TAG]).
     *
     * 연결이 끊기면 (EOF 등) 내부 세션을 비우고 throw → 상위(ViewModel) 가 재구독하면
     * [connect] 가 자동으로 새 세션을 만든다.
     */
    suspend fun subscribeMessages(buyOrNotId: Int): Flow<ChatMessageModel> {
        connect()
        val s = session ?: error("STOMP 세션이 준비되지 않았습니다")
        val destination = "/topic/chat/$buyOrNotId"
        Log.d(TAG, "subscribe: $destination")
        return s.subscribeText(destination)
            .onEach { json -> Log.d(TAG, "← receive[$destination]: $json") }
            .map { json -> gson.fromJson(json, ChatMessageRes::class.java).toDomainModel() }
            .catch { t ->
                Log.w(TAG, "subscribe[$destination]: stream broken, clearing session for reconnect", t)
                clearSessionOnError()
                throw t
            }
            .onCompletion { cause ->
                if (cause != null) {
                    Log.d(TAG, "subscribe[$destination]: terminated cause=${cause.javaClass.simpleName}")
                }
            }
    }

    /** 끊긴 세션을 정리해 다음 [connect] 호출 시 새 세션이 만들어지게 한다. */
    private suspend fun clearSessionOnError() {
        mutex.withLock {
            session = null
            _connectionState.value = State.Disconnected
        }
    }

    /** 메시지 전송. [content] 외 메타정보는 호출자가 currentUser 기반으로 채워서 보냄. */
    suspend fun send(buyOrNotId: Int, userId: String, username: String, content: String) {
        connect()
        val s = session ?: error("STOMP 세션이 준비되지 않았습니다")
        val payload = SendChatPayload(userId = userId, username = username, content = content)
        val json = gson.toJson(payload)
        val destination = "/app/chat/$buyOrNotId"
        Log.d(TAG, "→ send[$destination]: $json")
        try {
            s.sendText(destination, json)
            Log.d(TAG, "send[$destination]: ack ✓")
        } catch (t: Throwable) {
            Log.e(TAG, "send[$destination]: failed", t)
            throw t
        }
    }

    /** 세션 종료. 채팅방을 떠날 때 호출. */
    suspend fun disconnect() {
        mutex.withLock {
            Log.d(TAG, "disconnect()")
            try {
                session?.disconnect()
            } catch (t: Throwable) {
                Log.w(TAG, "disconnect(): error during close (ignored)", t)
            } finally {
                session = null
                _connectionState.value = State.Idle
            }
        }
    }

    private companion object {
        const val TAG = "StompChat"
    }

    /** 송신 페이로드 — 서버 스펙 `{userId, username, content}` */
    private data class SendChatPayload(
        val userId: String,
        val username: String,
        val content: String
    )
}
