package com.project.data.remote.request.buyornot

import com.google.gson.annotations.SerializedName

data class UpsertBuyOrNotPostingReq(
    @SerializedName("productName") val productName: String,
    @SerializedName("productPrice") val productPrice: Int,
    @SerializedName("productImageUrl") val productImageUrl: String,
    @SerializedName("goodReason") val goodReason: String,
    @SerializedName("badReason") val badReason: String,
)
