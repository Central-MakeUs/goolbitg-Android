package com.project.data.remote.request.challengegroup

import com.google.gson.annotations.SerializedName

data class ChallengeGroupEnrollReq(
    @SerializedName("password") val password: String?
)
