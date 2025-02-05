package com.project.domain.model.challenge

data class ChallengeRecordListModel(
    val totalSize: Int,
    val totalPages: Int,
    val size: Int,
    val page: Int,
    val items: List<ChallengeRecordModel>,
)