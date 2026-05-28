package com.project.presentation.buyornot.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.domain.model.DataState
import com.project.domain.usecase.buyornot.DisconnectChatUseCase
import com.project.domain.usecase.buyornot.FetchChatHistoryUseCase
import com.project.domain.usecase.buyornot.GetBuyOrNotPostingDetailUseCase
import com.project.domain.usecase.buyornot.ObserveChatMessagesUseCase
import com.project.domain.usecase.buyornot.ObserveLocalChatUseCase
import com.project.domain.usecase.buyornot.SendChatMessageUseCase
import com.project.domain.usecase.user.GetUserInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BuyOrNotChatRoomViewModel @Inject constructor(
    private val fetchChatHistoryUseCase: FetchChatHistoryUseCase,
    private val getPostingDetailUseCase: GetBuyOrNotPostingDetailUseCase,
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val observeChatMessagesUseCase: ObserveChatMessagesUseCase,
    private val observeLocalChatUseCase: ObserveLocalChatUseCase,
    private val sendChatMessageUseCase: SendChatMessageUseCase,
    private val disconnectChatUseCase: DisconnectChatUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(BuyOrNotChatRoomState())
    val state: StateFlow<BuyOrNotChatRoomState> = _state.asStateFlow()

    private val _navigateBack = MutableSharedFlow<Unit>()
    val navigateBack = _navigateBack.asSharedFlow()

    private var subscriptionJob: Job? = null
    private var localObserveJob: Job? = null

    fun onEvent(event: BuyOrNotChatRoomEvent) {
        when (event) {
            is BuyOrNotChatRoomEvent.InitPostId -> {
                _state.update { it.copy(postId = event.postId) }
                loadAll(event.postId)
            }
            is BuyOrNotChatRoomEvent.LoadMessages -> loadHistory(_state.value.postId)
            is BuyOrNotChatRoomEvent.ChangeInput -> _state.update { it.copy(inputText = event.text) }
            is BuyOrNotChatRoomEvent.SendMessage -> sendMessage()
            is BuyOrNotChatRoomEvent.ExitChat -> exitChat()
        }
    }

    private fun loadAll(postId: Int) {
        loadCurrentUser()
        loadPosting(postId)
        startLocalObservation(postId)   // DB Flow → UI 즉시 반영 (캐시된 메시지 보여주기)
        loadHistory(postId)             // REST → DB upsert (자동으로 위 Flow 통해 UI 갱신)
        startStompSubscription(postId)  // STOMP → DB upsert (마찬가지)
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            getUserInfoUseCase().collect { result ->
                if (result is DataState.Success) {
                    _state.update {
                        it.copy(
                            currentUserId = result.data?.id,
                            currentUsername = result.data?.nickname
                        )
                    }
                }
            }
        }
    }

    private fun loadPosting(postId: Int) {
        viewModelScope.launch {
            getPostingDetailUseCase(postId).collect { result ->
                if (result is DataState.Success) {
                    _state.update { it.copy(posting = result.data) }
                }
            }
        }
    }

    /** DB Flow 구독 — 진입 시 한 번만 시작하고 이후 모든 메시지 변경(REST/STOMP)이 자동 반영됨 */
    private fun startLocalObservation(postId: Int) {
        localObserveJob?.cancel()
        localObserveJob = viewModelScope.launch {
            observeLocalChatUseCase(postId).collect { messages ->
                _state.update { it.copy(messages = messages) }
            }
        }
    }

    private fun loadHistory(postId: Int) {
        android.util.Log.d("StompChat", "loadHistory(postId=$postId): REST fetch start")
        viewModelScope.launch {
            // chatLastId = null → 최신 페이지 fetch. 이후 페이지는 oldestId 기반으로 추가 가능.
            fetchChatHistoryUseCase(postId = postId, chatLastId = null).collect { result ->
                when (result) {
                    is DataState.Success -> {
                        val list = result.data ?: emptyList()
                        android.util.Log.d("StompChat", "loadHistory: ✓ count=${list.size} (DB 에 upsert 완료)")
                    }
                    is DataState.Error -> {
                        android.util.Log.e("StompChat", "loadHistory: error code=${result.code} msg=${result.message}")
                        _state.update { it.copy(errorMessage = result.message) }
                    }
                    is DataState.Exception -> {
                        android.util.Log.e("StompChat", "loadHistory: exception", result.e)
                        _state.update { it.copy(errorMessage = result.e.message) }
                    }
                    is DataState.Loading -> { /* DB Flow 로 갱신될 것이므로 별도 처리 X */ }
                }
            }
        }
    }

    private fun startStompSubscription(postId: Int) {
        subscriptionJob?.cancel()
        subscriptionJob = viewModelScope.launch {
            // 끊김(EOF 등) 발생 시 지수적 backoff 로 재구독 — 화면이 살아있는 동안 계속 시도.
            var attempt = 0
            while (isActive) {
                try {
                    observeChatMessagesUseCase(postId).collect { incoming ->
                        android.util.Log.d(
                            "StompChat",
                            "incoming: id=${incoming.id} from=${incoming.username} text=${incoming.content}"
                        )
                        attempt = 0 // 정상 수신했으면 backoff 카운터 리셋
                        // DB → observeLocalChat 으로 UI 자동 반영. 추가 작업 불필요.
                    }
                    // collect 가 정상 완료된 경우(서버가 stream 닫음): backoff 후 재시도
                } catch (ce: CancellationException) {
                    throw ce
                } catch (t: Throwable) {
                    android.util.Log.e("StompChat", "subscription failed (attempt=$attempt)", t)
                }
                attempt += 1
                val delayMs = (1000L * (1 shl (attempt - 1).coerceAtMost(4))).coerceAtMost(15_000L)
                android.util.Log.d("StompChat", "reconnect in ${delayMs}ms")
                delay(delayMs)
            }
        }
    }

    private fun sendMessage() {
        val s = _state.value
        if (!s.canSend) return
        val text = s.inputText.trim()
        val userId = s.currentUserId.orEmpty()
        val username = s.currentUsername.orEmpty()
        _state.update { it.copy(inputText = "") }
        viewModelScope.launch {
            try {
                sendChatMessageUseCase(
                    buyOrNotId = s.postId,
                    userId = userId,
                    username = username,
                    content = text
                )
                // 서버가 echo 로 broadcast → STOMP collect → DB upsert → observeLocalChat → UI
            } catch (ce: CancellationException) {
                throw ce
            } catch (t: Throwable) {
                android.util.Log.e("StompChat", "send failed", t)
                _state.update { it.copy(errorMessage = t.message) }
            }
        }
    }

    private fun exitChat() {
        viewModelScope.launch {
            try {
                disconnectChatUseCase()
            } catch (_: Throwable) {
                // 종료 실패해도 화면은 나간다
            }
            _navigateBack.emit(Unit)
        }
    }

    override fun onCleared() {
        super.onCleared()
        subscriptionJob?.cancel()
        localObserveJob?.cancel()
    }
}
