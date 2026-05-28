package com.project.data.remote.response.challengegroup

import com.google.gson.annotations.SerializedName
import com.project.domain.model.challengegroup.ChallengeGroupRecordModel

data class ChallengeGroupRecordRes(
    @SerializedName("challengeGroupId") val challengeGroupId: Int,
    @SerializedName("userId") val userId: String,
    @SerializedName("date") val date: String,
    @SerializedName("status") val status: String
) {
    fun toDomainModel() = ChallengeGroupRecordModel(
        challengeGroupId = challengeGroupId,
        userId = userId,
        date = date,
        status = status
    )
}
