package com.project.data.remote.response.challengegroup

import com.google.gson.annotations.SerializedName
import com.project.domain.model.challengegroup.ChallengeGroupModel

data class ChallengeGroupRes(
    @SerializedName("id") val id: Int,
    @SerializedName("ownerId") val ownerId: String,
    @SerializedName("title") val title: String,
    @SerializedName("reward") val reward: Int,
    @SerializedName("hashtags") val hashtags: List<String>,
    @SerializedName("category") val category: String?,
    @SerializedName("maxSize") val maxSize: Int,
    @SerializedName("peopleCount") val peopleCount: Int,
    @SerializedName("isHidden") val isHidden: Boolean,
    @SerializedName("password") val password: String?,
    @SerializedName("avgAchieveRatio") val avgAchieveRatio: Float,
    @SerializedName("maxAchieveDays") val maxAchieveDays: Int
) {
    fun toDomainModel() = ChallengeGroupModel(
        id = id,
        ownerId = ownerId,
        title = title,
        reward = reward,
        hashtags = hashtags,
        category = category ?: "",
        maxSize = maxSize,
        peopleCount = peopleCount,
        isHidden = isHidden,
        password = password,
        avgAchieveRatio = avgAchieveRatio,
        maxAchieveDays = maxAchieveDays
    )
}
