package com.project.data.remote.response.challenge

import com.google.gson.annotations.SerializedName
import com.project.domain.model.challenge.ChallengeListModel

data class ChallengeListRes(
    @SerializedName("totalSize") val totalSize: Int,
    @SerializedName("totalPages") val totalPages: Int,
    @SerializedName("size") val size: Int,
    @SerializedName("page") val page: Int,
    @SerializedName("items") val items: List<ChallengeInfoRes>,
) {
    fun toDomainModel() = ChallengeListModel(
        totalSize = totalSize,
        totalPages = totalPages,
        size = size,
        page = page,
        items = items.map { it.toDomainModel() },
    )
}

