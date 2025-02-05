package com.project.domain.model.challenge

data class ChallengeRecordModel(
    val challenge: ChallengeInfoModel,
    val userId: String,
    val date: String,
    val status: String,
    val location: Int,
)