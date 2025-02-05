package com.project.domain.model.challenge

data class ChallengeListModel(
    val totalSize: Int,
    val totalPages: Int,
    val size: Int,
    val page: Int,
    val items: List<ChallengeInfoModel>,
)