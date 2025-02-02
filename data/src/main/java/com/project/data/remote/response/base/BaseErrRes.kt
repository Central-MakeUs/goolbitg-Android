package com.project.data.remote.response.base

import com.google.gson.annotations.SerializedName

data class BaseErrRes(
    @SerializedName("code") val code: Int,
    @SerializedName("message") val msg: String
)
