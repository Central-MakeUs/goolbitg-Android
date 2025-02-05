package com.project.data.remote.response.challenge

import com.google.gson.annotations.SerializedName
import com.project.domain.model.challenge.ChallengeTripleModel

data class ChallengeTripleRes(
    @SerializedName("challenge") val challenge: ChallengeInfoRes,
    @SerializedName("duration") val duration: Int,
    @SerializedName("check1") val check1: String,
    @SerializedName("check2") val check2: String,
    @SerializedName("check3") val check3: String,
    @SerializedName("location") val location: Int,
    @SerializedName("canceled") val canceled: Boolean,
) {
    fun toDomainModel() = ChallengeTripleModel(
        challenge = challenge.toDomainModel(),
        duration = duration,
        check1 = check1,
        check2 = check2,
        check3 = check3,
        location = location,
        canceled = canceled,
    )
}