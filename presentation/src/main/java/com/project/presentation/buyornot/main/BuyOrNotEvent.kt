package com.project.presentation.buyornot.main

sealed class BuyOrNotEvent {
    data class ClickReportReason(val idx: Int) : BuyOrNotEvent()
    data object FetchMainNextPage : BuyOrNotEvent()
    data object FetchMyNextPage : BuyOrNotEvent()
    data class DeleteMyPosting(val postId: Int) : BuyOrNotEvent()
    data class VotePosting(val postId: Int, val isGood: Boolean) : BuyOrNotEvent()
    data class ModifyLocalPosting(
        val postId: Int,
        val productName: String,
        val price: Int,
        val imgUrl: String,
        val goodReason: String,
        val badReason: String
    ) : BuyOrNotEvent()

    data class ReportPosting(val postId: Int, val reason: String) : BuyOrNotEvent()
}
