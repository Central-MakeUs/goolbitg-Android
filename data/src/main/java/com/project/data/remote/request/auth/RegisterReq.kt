package com.project.data.remote.request.auth

import com.google.gson.annotations.SerializedName

data class RegisterReq(
    @SerializedName("type") val type: String,
    @SerializedName("idToken") val idToken: String,
)
