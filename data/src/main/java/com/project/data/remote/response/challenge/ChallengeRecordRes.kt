package com.project.data.remote.response.challenge

import com.google.gson.annotations.SerializedName
import com.project.domain.model.challenge.ChallengeRecordModel

data class ChallengeRecordRes(
    @SerializedName("challenge") val challenge: ChallengeInfoRes,
    @SerializedName("userId") val userId: String,
    @SerializedName("date") val date: String,
    @SerializedName("status") val status: String,
    @SerializedName("location") val location: Int,
) {
    fun toDomainModel() = ChallengeRecordModel(
        challenge = challenge.toDomainModel(),
        userId = userId,
        date = date,
        status = status,
        location = location,
    )
}
