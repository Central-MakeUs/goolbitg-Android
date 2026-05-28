package com.project.domain.model.buyornot

data class ChatMessageModel(
    val id: Int,
    /** 채팅 대상이 되는 살까말까 게시물 id (서버 응답에 포함) */
    val buyOrNotId: Int? = null,
    /** 작성자의 사용자 식별자 (본인 메시지 판별용) */
    val userId: String? = null,
    val username: String,
    val content: String,
    val sentDateTime: String
)
