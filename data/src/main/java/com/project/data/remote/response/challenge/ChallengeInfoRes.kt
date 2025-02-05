package com.project.data.remote.response.challenge

import com.google.gson.annotations.SerializedName
import com.project.domain.model.challenge.ChallengeInfoModel

data class ChallengeInfoRes(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("imageUrlSmall") val imageUrlSmall: String,
    @SerializedName("imageUrlLarge") val imageUrlLarge: String,
    @SerializedName("reward") val reward: Int,
    @SerializedName("participantCount") val participantCount: Int,
    @SerializedName("avgAchieveRatio") val avgAchieveRatio: Int,
    @SerializedName("maxAchieveDays") val maxAchieveDays: Int,
) {
    fun toDomainModel() = ChallengeInfoModel(
        id = id,
        title = title,
        imageUrlSmall = imageUrlSmall,
        imageUrlLarge = imageUrlLarge,
        reward = reward,
        participantCount = participantCount,
        avgAchieveRatio = avgAchieveRatio,
        maxAchieveDays = maxAchieveDays,
    )
}