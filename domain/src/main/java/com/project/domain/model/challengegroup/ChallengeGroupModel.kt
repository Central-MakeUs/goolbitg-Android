package com.project.domain.model.challengegroup

data class ChallengeGroupModel(
    val id: Int,
    val ownerId: String,
    val title: String,
    val reward: Int,
    val hashtags: List<String>,
    val category: String,
    val maxSize: Int,
    val peopleCount: Int,
    val isHidden: Boolean,
    val password: String?,
    val avgAchieveRatio: Float,
    val maxAchieveDays: Int
)
