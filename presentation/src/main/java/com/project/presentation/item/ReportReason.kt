package com.project.presentation.item

data class ReportReason(
    val reasonEnum: ReportReasonEnum,
    val isSelected: Boolean = false,
)
