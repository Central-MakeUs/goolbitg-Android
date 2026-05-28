package com.project.domain.model.challengegroup

data class ChallengeGroupRankModel(
    val group: ChallengeGroupModel,
    val rank: List<ChallengeGroupRankItemModel>
)
