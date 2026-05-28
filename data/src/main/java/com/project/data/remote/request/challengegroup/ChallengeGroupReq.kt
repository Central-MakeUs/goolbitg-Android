package com.project.data.remote.request.challengegroup

import com.google.gson.annotations.SerializedName

data class ChallengeGroupReq(
    @SerializedName("title") val title: String,
    @SerializedName("reward") val reward: Int,
    @SerializedName("hashtags") val hashtags: List<String>,
    @SerializedName("category") val category: String,
    @SerializedName("maxSize") val maxSize: Int,
    @SerializedName("isHidden") val isHidden: Boolean,
    @SerializedName("password") val password: String?
)
