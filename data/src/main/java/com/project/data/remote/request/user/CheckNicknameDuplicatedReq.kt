package com.project.data.remote.request.user

import com.google.gson.annotations.SerializedName

data class CheckNicknameDuplicatedReq(
    @SerializedName("nickname") val nickname: String
)
