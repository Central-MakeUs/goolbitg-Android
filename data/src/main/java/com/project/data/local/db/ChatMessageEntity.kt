package com.project.data.local.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.project.domain.model.buyornot.ChatMessageModel

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey val id: Int,
    val buyOrNotId: Int,
    val userId: String?,
    val username: String,
    val content: String,
    val sentDateTime: String
) {
    fun toDomainModel(): ChatMessageModel = ChatMessageModel(
        id = id,
        buyOrNotId = buyOrNotId,
        userId = userId,
        username = username,
        content = content,
        sentDateTime = sentDateTime
    )

    companion object {
        fun fromDomainModel(buyOrNotIdFallback: Int, model: ChatMessageModel): ChatMessageEntity =
            ChatMessageEntity(
                id = model.id,
                buyOrNotId = model.buyOrNotId ?: buyOrNotIdFallback,
                userId = model.userId,
                username = model.username,
                content = model.content,
                sentDateTime = model.sentDateTime
            )
    }
}
