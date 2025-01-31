package com.project.presentation.onboarding

import com.project.presentation.common.DayOfWeekEnum

sealed class OnboardingEvent {
    data class ChangeNickname(val newValue: String): OnboardingEvent()
    data class ChangeYear(val newValue: String): OnboardingEvent()
    data class ChangeMonth(val newValue: String): OnboardingEvent()
    data class ChangeDay(val newValue: String): OnboardingEvent()
    data object ClickMale: OnboardingEvent()
    data object ClickFemale: OnboardingEvent()
    data class ClickCheckListItem(val idx: Int): OnboardingEvent()
    data class ChangeMonthAvgIncome(val newValue: String): OnboardingEvent()
    data class ChangeMonthAvgSaving(val newValue: String): OnboardingEvent()
    data class SelectMajorExpenditureDayOfWeek(val dayOfWeek: DayOfWeekEnum): OnboardingEvent()
    data class ChangeMajorExpenditureHour(val newValue: String): OnboardingEvent()
    data class ChangeMajorExpenditureMinute(val newValue: String): OnboardingEvent()
    data class ChangeMajorExpenditureAmpm(val newValue: String): OnboardingEvent()
}