package com.project.presentation.item

import androidx.annotation.StringRes
import com.project.presentation.R

enum class WithdrawEnum(@StringRes val strId: Int) {
    Reason1(R.string.withdraw_reason_1),
    Reason2(R.string.withdraw_reason_2),
    Reason3(R.string.withdraw_reason_3),
    Reason4(R.string.withdraw_reason_4),
    Etc(R.string.withdraw_reason_5)
}