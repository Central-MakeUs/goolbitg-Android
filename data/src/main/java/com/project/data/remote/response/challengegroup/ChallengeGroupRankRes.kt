package com.project.data.remote.response.challengegroup

import com.google.gson.annotations.SerializedName
import com.project.domain.model.challengegroup.ChallengeGroupRankItemModel
import com.project.domain.model.challengegroup.ChallengeGroupRankModel

data class ChallengeGroupRankRes(
    @SerializedName("group") val group: ChallengeGroupRes,
    @SerializedName("rank") val rank: List<RankItemRes>
) {
    fun toDomainModel() = ChallengeGroupRankModel(
        group = group.toDomainModel(),
        rank = rank.map { it.toDomainModel() }
    )
}

data class RankItemRes(
    @SerializedName("name") val name: String,
    @SerializedName("profileUrl") val profileUrl: String,
    @SerializedName("saving") val saving: Int
) {
    fun toDomainModel() = ChallengeGroupRankItemModel(
        name = name,
        profileUrl = profileUrl,
        saving = saving
    )
}
