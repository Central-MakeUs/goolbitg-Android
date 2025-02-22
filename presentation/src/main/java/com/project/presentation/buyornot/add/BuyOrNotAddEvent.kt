package com.project.presentation.buyornot.add

sealed class BuyOrNotAddEvent {
    data class ChangePostId(val postId: Int?) : BuyOrNotAddEvent()
    data class ChangeImgUrl(val newValue: String) : BuyOrNotAddEvent()
    data class ChangeProductName(val newValue: String) : BuyOrNotAddEvent()
    data class ChangePrice(val newValue: String) : BuyOrNotAddEvent()
    data class ChangeGoodReason(val newValue: String) : BuyOrNotAddEvent()
    data class ChangeBadReason(val newValue: String) : BuyOrNotAddEvent()
    data class ChangeCropUri(val uri: String) : BuyOrNotAddEvent()
    data object InitImgUrl : BuyOrNotAddEvent()
    data object RequestAddPosting : BuyOrNotAddEvent()
}
