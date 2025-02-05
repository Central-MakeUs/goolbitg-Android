package com.project.domain.model.challenge

data class ChallengeTripleModel(
    val challenge: ChallengeInfoModel,
    val duration: Int,
    val check1: String,
    val check2: String,
    val check3: String,
    val location: Int,
    val canceled: Boolean,
)