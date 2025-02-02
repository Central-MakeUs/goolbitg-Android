package com.project.domain.model.user

data class WeeklyRecordStatusModel (
    val nickname: String?,
    val saving: Int,
    val continueCount: Int?,
    val weeklyStatus: List<WeeklyStatusModel>,
    val todayIndex: Int
)

data class WeeklyStatusModel(
    val totalChallenges: Int,
    val achievedChallenges: Int
)
