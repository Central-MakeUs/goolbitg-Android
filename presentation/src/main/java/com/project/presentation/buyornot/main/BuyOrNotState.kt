package com.project.presentation.buyornot.main

import com.project.domain.model.buyornot.BuyOrNotPostingModel
import com.project.presentation.item.ReportReason
import com.project.presentation.item.ReportReasonEnum

data class BuyOrNotState(
    val tabIdx: Int = 0,
    val mainPostList: List<BuyOrNotPostingModel> = listOf(),
    val mainPostPage: Int = 0,
    val mainPostSize: Int = 10,
    val myPostList: List<BuyOrNotPostingModel> = listOf(),
    val myPostPage: Int = 0,
    val myPostSize: Int = 10,

    val reportList: List<ReportReason> = ReportReasonEnum.entries.map { ReportReason(reasonEnum = it) },
    val reportResult: String? = null,

    val isMainPostsLoading: Boolean = false,
    val isMyPostsLoading: Boolean = false
)
