package com.project.data.remote.response.auth

import com.google.gson.annotations.SerializedName
import com.project.domain.model.auth.LoginModel

data class LoginRes(
    @SerializedName("accessToken") val accessToken: String,
    @SerializedName("refreshToken") val refreshToken: String,
) {
    fun toDomainModel() =
        LoginModel(
            accessToken = accessToken,
            refreshToken = refreshToken
        )
}
