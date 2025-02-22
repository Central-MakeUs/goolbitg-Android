package com.project.data.remote.request.buyornot

import com.google.gson.annotations.SerializedName

data class ReportPostingReq(
    @SerializedName("reason") val reason: String
)
