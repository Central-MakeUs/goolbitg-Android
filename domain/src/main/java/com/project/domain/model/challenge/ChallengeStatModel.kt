package com.project.domain.model.challenge

data class ChallengeStatModel(
    val challengeId: Int,
    val userId: String,
    val continueCount: Int?,
    val totalCount: Int,
    val enrollCount: Int,
)