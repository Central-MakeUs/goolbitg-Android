package com.project.presentation.item

import androidx.annotation.StringRes
import com.project.presentation.R

enum class ReportReasonEnum(@StringRes val strId: Int) {
    Sexual(strId = R.string.buyornot_report_reason_1),
    Violent(strId = R.string.buyornot_report_reason_2),
    Wrong(strId = R.string.buyornot_report_reason_3),
    Etc(strId = R.string.buyornot_report_reason_4),
}
