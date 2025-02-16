package com.project.presentation.buyornot

sealed class BuyOrNotEvent {
    data class ClickReportReason(val idx: Int): BuyOrNotEvent()
    data object FetchMainNextPage: BuyOrNotEvent()
    data object FetchMyNextPage: BuyOrNotEvent()
    data class DeleteMyPosting(val postId: Int): BuyOrNotEvent()
}
