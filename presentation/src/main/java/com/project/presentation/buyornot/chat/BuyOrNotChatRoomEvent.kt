package com.project.presentation.buyornot.chat

sealed class BuyOrNotChatRoomEvent {
    data class InitPostId(val postId: Int) : BuyOrNotChatRoomEvent()
    object LoadMessages : BuyOrNotChatRoomEvent()
    data class ChangeInput(val text: String) : BuyOrNotChatRoomEvent()
    object SendMessage : BuyOrNotChatRoomEvent()
    object ExitChat : BuyOrNotChatRoomEvent()
}
