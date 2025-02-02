package com.project.data.remote.response.auth

import com.google.gson.annotations.SerializedName
import com.project.domain.model.auth.RefreshTokenModel

data class RefreshTokenRes(
    @SerializedName("accessToken") val accessToken: String,
    @SerializedName("refreshToken") val refreshToken: String,
){
    fun toDomainModel() =
        RefreshTokenModel(
            accessToken = accessToken,
            refreshToken = refreshToken,
        )
}
