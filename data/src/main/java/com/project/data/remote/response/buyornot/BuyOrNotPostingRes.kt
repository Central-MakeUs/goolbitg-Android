package com.project.data.remote.response.buyornot

import com.google.gson.annotations.SerializedName
import com.project.domain.model.buyornot.BuyOrNotPostingModel

data class BuyOrNotPostingRes(
    @SerializedName("id") val id: Int,
    @SerializedName("writerId") val writerId: String,
    @SerializedName("productName") val productName: String,
    @SerializedName("productPrice") val productPrice: Int,
    @SerializedName("productImageUrl") val productImageUrl: String,
    @SerializedName("goodReason") val goodReason: String,
    @SerializedName("badReason") val badReason: String,
    @SerializedName("goodVoteCount") val goodVoteCount: Int,
    @SerializedName("badVoteCount") val badVoteCount: Int,
) {
    fun toDomainModel() = BuyOrNotPostingModel(
        id = id,
        writerId = writerId,
        productName = productName,
        productPrice = productPrice,
        productImageUrl = productImageUrl,
        goodReason = goodReason,
        badReason = badReason,
        goodVoteCount = goodVoteCount,
        badVoteCount = badVoteCount,
    )
}
