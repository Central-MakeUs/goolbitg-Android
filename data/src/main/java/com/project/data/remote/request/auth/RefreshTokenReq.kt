package com.project.data.remote.request.auth

import com.google.gson.annotations.SerializedName

data class RefreshTokenReq(
    @SerializedName("refreshToken") val refreshToken: String
)
