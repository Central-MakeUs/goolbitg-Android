package com.project.data.remote.response.challenge

import com.google.gson.annotations.SerializedName
import com.project.domain.model.challenge.ChallengeStatModel

data class ChallengeStatRes(
    @SerializedName("challengeId") val challengeId: Int,
    @SerializedName("userId") val userId: String,
    @SerializedName("continueCount") val continueCount: Int,
    @SerializedName("totalCount") val totalCount: Int,
    @SerializedName("enrollCount") val enrollCount: Int,
) {
    fun toDomainModel() = ChallengeStatModel(
        challengeId = challengeId,
        userId = userId,
        continueCount = continueCount,
        totalCount = totalCount,
        enrollCount = enrollCount,
    )
}