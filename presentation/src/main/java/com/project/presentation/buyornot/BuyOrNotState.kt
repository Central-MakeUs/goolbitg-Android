package com.project.presentation.buyornot

import com.project.domain.model.buyornot.BuyOrNotPostingModel
import com.project.presentation.item.ReportReason
import com.project.presentation.item.ReportReasonEnum

data class BuyOrNotState(
    val buyOrNotPostList: List<BuyOrNotPostingModel> = listOf(),
    val page: Int = 0,
    val size: Int = 20,

    val reportList: List<ReportReason> = ReportReasonEnum.entries.map { ReportReason(reasonEnum = it) },
    val isLoading: Boolean = false
)
