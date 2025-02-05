package com.project.presentation.item

import androidx.annotation.StringRes
import com.project.presentation.R

enum class CheckListEnum(@StringRes val strId: Int) {
    CheckListInfo1(strId = R.string.onboarding_third_checklist_1),
    CheckListInfo2(strId = R.string.onboarding_third_checklist_2),
    CheckListInfo3(strId = R.string.onboarding_third_checklist_3),
    CheckListInfo4(strId = R.string.onboarding_third_checklist_4),
    CheckListInfo5(strId = R.string.onboarding_third_checklist_5),
    CheckListInfo6(strId = R.string.onboarding_third_checklist_6),
}