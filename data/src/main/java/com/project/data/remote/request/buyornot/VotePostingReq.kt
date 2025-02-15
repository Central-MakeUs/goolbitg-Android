package com.project.data.remote.request.buyornot

import com.google.gson.annotations.SerializedName

data class VotePostingReq(
    @SerializedName("vote") val vote: String
)
