package com.project.data.remote.response.buyornot

import com.google.gson.annotations.SerializedName
import com.project.domain.model.buyornot.ChatMessageModel

/**
 * 서버 응답 형식:
 * {"id":101,"buyOrNotId":2,"userId":"id0001","username":"nickname","content":"...","sentDateTime":"..."}
 */
data class ChatMessageRes(
    @SerializedName("id") val id: Int,
    @SerializedName("buyOrNotId") val buyOrNotId: Int? = null,
    @SerializedName("userId") val userId: String? = null,
    @SerializedName("username") val username: String,
    @SerializedName("content") val content: String,
    @SerializedName("sentDateTime") val sentDateTime: String
) {
    fun toDomainModel() = ChatMessageModel(
        id = id,
        buyOrNotId = buyOrNotId,
        userId = userId,
        username = username,
        content = content,
        sentDateTime = sentDateTime
    )
}
