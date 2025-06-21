package com.project.data.remote.response.challenge

import com.google.gson.annotations.SerializedName
import com.project.domain.model.challenge.ChallengeGroupItemModel

data class FetchChallengeGroupsRes(
    @SerializedName("totalSize") val totalSize: Int,
    @SerializedName("totalPages") val totalPages: Int,
    @SerializedName("size") val size: Int,
    @SerializedName("page") val page: Int,
    @SerializedName("items") val items: List<ChallengeGroupItemRes>,
)

data class ChallengeGroupItemRes(
    @SerializedName("id") val id: Int,
    @SerializedName("ownerId") val ownerId: String,
    @SerializedName("title") val title: String,
    @SerializedName("reward") val reward: Int,
    @SerializedName("hashtags") val hashtags: List<String>,
    @SerializedName("maxSize") val maxSize: Int,
    @SerializedName("peopleCount") val peopleCount: Int,
    @SerializedName("isHidden") val isHidden: Boolean,
    @SerializedName("password") val password: String?,
    @SerializedName("avgAchieveRatio") val avgAchieveRatio: Int,
    @SerializedName("maxAchieveDays") val maxAchieveDays: Int,
) {
    fun toDomainModel(): ChallengeGroupItemModel =
        ChallengeGroupItemModel(
            id = id,
            ownerId = ownerId,
            title = title,
            reward = reward,
            hashtags = hashtags,
            maxSize = maxSize,
            peopleCount = peopleCount,
            isHidden = isHidden,
            password = password,
            avgAchieveRatio = avgAchieveRatio,
            maxAchieveDays = maxAchieveDays
        )
}
