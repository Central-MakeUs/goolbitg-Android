package com.project.domain.repository

import com.project.domain.model.DataState
import com.project.domain.model.buyornot.BuyOrNotPostingModel
import com.project.domain.model.buyornot.ChatMessageModel
import com.project.domain.model.buyornot.FetchBuyOrNotPostsModel
import com.project.domain.model.buyornot.VotePostingModel
import kotlinx.coroutines.flow.Flow

interface BuyOrNotRepository {
    suspend fun fetchBuyOrNotPosts(
        page: Int,
        size: Int,
        isCreated: Boolean
    ): Flow<DataState<FetchBuyOrNotPostsModel>>

    suspend fun postBuyOrNotPosting(
        productName: String,
        productPrice: Int,
        productImageUrl: String,
        goodReason: String,
        badReason: String,
    ): Flow<DataState<BuyOrNotPostingModel>>

    suspend fun modifyBuyOrNotPosting(
        postId: Int,
        productName: String,
        productPrice: Int,
        productImageUrl: String,
        goodReason: String,
        badReason: String,
    ): Flow<DataState<BuyOrNotPostingModel>>

    suspend fun getBuyOrNotPostingDetail(postId: Int): Flow<DataState<BuyOrNotPostingModel>>
    suspend fun deleteBuyOrNotPosting(postId: Int): Flow<DataState<Boolean>>
    suspend fun reportPosting(postId: Int, reason: String): Flow<DataState<Boolean>>
    suspend fun votePosting(postId: Int, vote: String): Flow<DataState<VotePostingModel>>
    suspend fun uploadImage(byteArray: ByteArray): Flow<DataState<String>>
    suspend fun fetchChatList(page: Int, size: Int): Flow<DataState<FetchBuyOrNotPostsModel>>
    suspend fun fetchChatHistory(postId: Int, chatLastId: Int? = null): Flow<DataState<List<ChatMessageModel>>>

    /** LocalDB 의 채팅 메시지를 시간 오름차순으로 관찰 (UI 가 직접 구독) */
    fun observeLocalChat(postId: Int): Flow<List<ChatMessageModel>>

    /** STOMP 토픽 구독 — 새 채팅 메시지가 도착할 때마다 emit (Repository 가 DB 에도 자동 upsert) */
    suspend fun observeChatMessages(buyOrNotId: Int): Flow<ChatMessageModel>

    /** STOMP send destination 으로 메시지 전송 */
    suspend fun sendChatMessage(buyOrNotId: Int, userId: String, username: String, content: String)

    /** STOMP 세션 종료 (채팅방 떠날 때) */
    suspend fun disconnectChat()
}
