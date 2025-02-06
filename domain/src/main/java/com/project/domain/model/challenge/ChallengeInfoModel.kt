package com.project.domain.model.challenge

data class ChallengeInfoModel(
    val id: Int,
    val title: String,
    val imageUrlSmall: String,
    val imageUrlLarge: String,
    val reward: Int,
    val participantCount: Int,
    val avgAchieveRatio: Float,
    val maxAchieveDays: Int,
)