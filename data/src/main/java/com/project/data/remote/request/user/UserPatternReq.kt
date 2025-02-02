package com.project.data.remote.request.user

import com.google.gson.annotations.SerializedName

data class UserPatternReq(
    @SerializedName("primeUseDay") val primeUseDay: String,
    @SerializedName("primeUserTime") val primeUserTime: String
)
