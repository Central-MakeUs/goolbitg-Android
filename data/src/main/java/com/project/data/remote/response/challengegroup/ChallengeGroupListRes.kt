package com.project.data.remote.response.challengegroup

import com.google.gson.annotations.SerializedName
import com.project.domain.model.challengegroup.ChallengeGroupListModel

data class ChallengeGroupListRes(
    @SerializedName("totalSize") val totalSize: Int,
    @SerializedName("totalPages") val totalPages: Int,
    @SerializedName("size") val size: Int,
    @SerializedName("page") val page: Int,
    @SerializedName("items") val items: List<ChallengeGroupRes>
) {
    fun toDomainModel() = ChallengeGroupListModel(
        totalSize = totalSize,
        totalPages = totalPages,
        size = size,
        page = page,
        items = items.map { it.toDomainModel() }
    )
}
