package com.project.data.remote.request.auth

import com.google.gson.annotations.SerializedName

data class WithdrawReq(
    @SerializedName("reason") val reason: String
)