package com.project.presentation.buyornot

sealed class BuyOrNotEvent {
    data class ClickReportReason(val idx: Int): BuyOrNotEvent()
    data object FetchNextPage: BuyOrNotEvent()
}
