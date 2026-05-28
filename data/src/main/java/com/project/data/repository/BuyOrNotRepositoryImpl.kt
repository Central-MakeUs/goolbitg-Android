package com.project.data.repository

import com.project.data.local.db.ChatMessageDao
import com.project.data.local.db.ChatMessageEntity
import com.project.data.remote.common.NetworkUtils
import com.project.data.remote.datasource.BuyOrNotDataSource
import com.project.data.remote.datasource.UtilDataSource
import com.project.data.remote.request.buyornot.ReportPostingReq
import com.project.data.remote.request.buyornot.UpsertBuyOrNotPostingReq
import com.project.data.remote.request.buyornot.VotePostingReq
import com.project.domain.model.DataState
import com.project.domain.model.buyornot.BuyOrNotPostingModel
import com.project.domain.model.buyornot.ChatMessageModel
import com.project.domain.model.buyornot.FetchBuyOrNotPostsModel
import com.project.domain.model.buyornot.VotePostingModel
import com.project.domain.repository.BuyOrNotRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

class BuyOrNotRepositoryImpl @Inject constructor(
    private val buyOrNotDataSource: BuyOrNotDataSource,
    private val utilDataSource: UtilDataSource,
    private val stompChatClient: com.project.data.remote.socket.StompChatClient,
    private val chatMessageDao: ChatMessageDao
) : BuyOrNotRepository {
    override suspend fun fetchBuyOrNotPosts(
        page: Int,
        size: Int,
        isCreated: Boolean
    ): Flow<DataState<FetchBuyOrNotPostsModel>> {
        return NetworkUtils.handleApi(
            execute = {
                buyOrNotDataSource.fetchBuyOrNotPosts(
                    page = page,
                    size = size,
                    isCreated = isCreated
                )
            },
            mapper = { it?.toDomainModel() }
        )
    }

    override suspend fun postBuyOrNotPosting(
        productName: String,
        productPrice: Int,
        productImageUrl: String,
        goodReason: String,
        badReason: String
    ): Flow<DataState<BuyOrNotPostingModel>> {
        return NetworkUtils.handleApi(
            execute = {
                buyOrNotDataSource.postBuyOrNotPosting(
                    body = UpsertBuyOrNotPostingReq(
                        productName = productName,
                        productPrice = productPrice,
                        productImageUrl = productImageUrl,
                        goodReason = goodReason,
                        badReason = badReason,
                    )
                )
            },
            mapper = { it?.toDomainModel() }
        )

    }

    override suspend fun modifyBuyOrNotPosting(
        postId: Int,
        productName: String,
        productPrice: Int,
        productImageUrl: String,
        goodReason: String,
        badReason: String
    ): Flow<DataState<BuyOrNotPostingModel>> {
        return NetworkUtils.handleApi(
            execute = {
                buyOrNotDataSource.modifyBuyOrNotPosting(
                    postId = postId,
                    body = UpsertBuyOrNotPostingReq(
                        productName = productName,
                        productPrice = productPrice,
                        productImageUrl = productImageUrl,
                        goodReason = goodReason,
                        badReason = badReason,
                    )
                )
            },
            mapper = { it?.toDomainModel() }
        )

    }

    override suspend fun getBuyOrNotPostingDetail(postId: Int): Flow<DataState<BuyOrNotPostingModel>> {
        return NetworkUtils.handleApi(
            execute = {
                buyOrNotDataSource.getBuyOrNotPostingDetail(postId = postId)
            },
            mapper = { it?.toDomainModel() }
        )

    }

    override suspend fun deleteBuyOrNotPosting(postId: Int): Flow<DataState<Boolean>> {
        return NetworkUtils.handleApi(
            execute = {
                buyOrNotDataSource.deleteBuyOrNotPosting(postId = postId)
            },
            mapper = { true }
        )

    }

    override suspend fun reportPosting(postId: Int, reason: String): Flow<DataState<Boolean>> {
        return NetworkUtils.handleApi(
            execute = {
                buyOrNotDataSource.reportPosting(postId = postId, body = ReportPostingReq(reason = reason))
            },
            mapper = { true }
        )
    }

    override suspend fun votePosting(postId: Int, vote: String): Flow<DataState<VotePostingModel>> {
        return NetworkUtils.handleApi(
            execute = {
                buyOrNotDataSource.votePosting(
                    postId = postId,
                    body = VotePostingReq(vote = vote)
                )
            },
            mapper = { it?.toDomainModel() }
        )
    }

    override suspend fun uploadImage(byteArray: ByteArray): Flow<DataState<String>> {
        return NetworkUtils.handleApi(
            execute = {
                val requestBody = byteArray.toRequestBody("image/jpeg".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("image", "Image", requestBody)
                utilDataSource.uploadImg(
                    image = body
                )
            },
            mapper = { it?.url }
        )
    }

    override suspend fun fetchChatList(page: Int, size: Int): Flow<DataState<FetchBuyOrNotPostsModel>> =
        NetworkUtils.handleApi(
            execute = { buyOrNotDataSource.fetchChatList(page = page, size = size) },
            mapper = { it?.toDomainModel() }
        )

    /**
     * REST 채팅 히스토리 fetch.
     * 응답이 도착하면 LocalDB 에 upsert 까지 책임진다 → UI 는 [observeLocalChat] 만 보면 충분.
     */
    override suspend fun fetchChatHistory(postId: Int, chatLastId: Int?): Flow<DataState<List<ChatMessageModel>>> =
        NetworkUtils.handleApi(
            execute = { buyOrNotDataSource.fetchChatHistory(postId = postId, chatLastId = chatLastId) },
            mapper = { resList -> resList?.map { it.toDomainModel() } }
        ).onEach { state ->
            if (state is DataState.Success) {
                val list = state.data
                if (!list.isNullOrEmpty()) {
                    val entities = list.map { ChatMessageEntity.fromDomainModel(postId, it) }
                    chatMessageDao.upsertAll(entities)
                }
            }
        }

    override fun observeLocalChat(postId: Int): Flow<List<ChatMessageModel>> =
        chatMessageDao.observeByPost(postId).map { entities -> entities.map { it.toDomainModel() } }

    /**
     * STOMP 토픽 구독. 메시지가 들어올 때마다 LocalDB 에 upsert 후 그대로 emit.
     * UI 는 일반적으로 [observeLocalChat] 만 구독하지만, 디버깅/별도 처리용으로 raw flow 도 유지.
     */
    override suspend fun observeChatMessages(buyOrNotId: Int): Flow<ChatMessageModel> =
        stompChatClient.subscribeMessages(buyOrNotId).onEach { incoming ->
            chatMessageDao.upsert(ChatMessageEntity.fromDomainModel(buyOrNotId, incoming))
        }

    override suspend fun sendChatMessage(
        buyOrNotId: Int,
        userId: String,
        username: String,
        content: String
    ) {
        stompChatClient.send(buyOrNotId, userId, username, content)
    }

    override suspend fun disconnectChat() {
        stompChatClient.disconnect()
    }
}
