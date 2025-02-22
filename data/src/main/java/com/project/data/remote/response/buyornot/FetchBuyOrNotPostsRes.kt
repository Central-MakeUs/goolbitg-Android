package com.project.data.remote.response.buyornot

import com.google.gson.annotations.SerializedName
import com.project.domain.model.buyornot.FetchBuyOrNotPostsModel

data class FetchBuyOrNotPostsRes(
    @SerializedName("totalSize") val totalSize: Int,
    @SerializedName("totalPages") val totalPages: Int,
    @SerializedName("size") val size: Int,
    @SerializedName("page") val page: Int,
    @SerializedName("items") val items: List<BuyOrNotPostingRes>
) {
    fun toDomainModel() = FetchBuyOrNotPostsModel(
        totalSize = totalSize,
        totalPages = totalPages,
        size = size,
        page = page,
        items = items.map { it.toDomainModel() },
    )
}
