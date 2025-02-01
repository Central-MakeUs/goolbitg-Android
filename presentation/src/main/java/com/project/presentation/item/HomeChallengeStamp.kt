package com.project.presentation.item

import java.time.LocalDate

data class HomeChallengeStamp(
    val date: LocalDate,
    val isToday: Boolean,
    val isComplete: Boolean
)
