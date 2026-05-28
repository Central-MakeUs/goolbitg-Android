package com.project.data.remote.response.challengegroup

import com.google.gson.annotations.SerializedName
import com.project.domain.model.challengegroup.ChallengeGroupTrippleModel

data class ChallengeGroupTrippleRes(
    @SerializedName("duration") val duration: Int,
    @SerializedName("check1") val check1: String,
    @SerializedName("check2") val check2: String,
    @SerializedName("check3") val check3: String,
    @SerializedName("location") val location: Int,
    @SerializedName("canceled") val canceled: Boolean
) {
    fun toDomainModel() = ChallengeGroupTrippleModel(
        duration = duration,
        check1 = check1,
        check2 = check2,
        check3 = check3,
        location = location,
        canceled = canceled
    )
}
