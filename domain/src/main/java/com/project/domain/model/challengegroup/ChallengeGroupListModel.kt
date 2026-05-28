package com.project.domain.model.challengegroup

data class ChallengeGroupListModel(
    val totalSize: Int,
    val totalPages: Int,
    val size: Int,
    val page: Int,
    val items: List<ChallengeGroupModel>
)
