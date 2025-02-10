package com.project.presentation.challenge.main

import java.time.LocalDate

sealed class ChallengeEvent {
    data class ChangeSelectedDate(val date: LocalDate): ChallengeEvent()
    data class ChangePage(val offset: Long, val targetDate: LocalDate): ChallengeEvent()
}