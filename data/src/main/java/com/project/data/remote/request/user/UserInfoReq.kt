package com.project.data.remote.request.user

import com.google.gson.annotations.SerializedName

data class UserInfoReq(
    @SerializedName("nickname") val nickname: String,
    @SerializedName("birthday") val birthday: String?,
    @SerializedName("gender") val gender: String?
)
