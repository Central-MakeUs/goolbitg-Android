package com.project.presentation.item

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import com.project.presentation.R
import com.project.presentation.ui.theme.gray200
import com.project.presentation.ui.theme.gray400
import com.project.presentation.ui.theme.main100

enum class ChallengeDetailCheckEnum(@DrawableRes val drawableId: Int, val textColor: Color){
    Disable(R.drawable.ic_challenge_detail_disabled, gray400),
    Enable(R.drawable.ic_challenge_detail_enabled, gray200),
    Check(R.drawable.ic_challenge_detail_checked, main100)
}
