package com.project.presentation.onboarding

sealed class OnboardingEvent {
    data class ChangeNickname(val newValue: String): OnboardingEvent()
    data class ChangeYear(val newValue: String): OnboardingEvent()
    data class ChangeMonth(val newValue: String): OnboardingEvent()
    data class ChangeDay(val newValue: String): OnboardingEvent()
    data object ClickMale: OnboardingEvent()
    data object ClickFemale: OnboardingEvent()
}