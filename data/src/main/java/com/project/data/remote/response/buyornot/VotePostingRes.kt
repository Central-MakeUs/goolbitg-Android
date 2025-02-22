package com.project.data.remote.response.buyornot

import com.google.gson.annotations.SerializedName
import com.project.domain.model.buyornot.VotePostingModel

data class VotePostingRes(
    @SerializedName("goodVoteCount") val goodVoteCount: Int,
    @SerializedName("badVoteCount") val badVoteCount: Int
) {
    fun toDomainModel() = VotePostingModel(
        goodVoteCount = goodVoteCount,
        badVoteCount = badVoteCount,
    )
}
