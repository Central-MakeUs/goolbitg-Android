package com.project.presentation.onboarding

import com.project.presentation.common.GenderEnum

data class OnboardingState(
    val nickname: String,
    val year: String,
    val month: String,
    val day: String,
    val gender: GenderEnum?
) {
    companion object {
        fun create() = OnboardingState(
            nickname = "",
            year = "",
            month = "",
            day = "",
            gender = null
        )
    }
}