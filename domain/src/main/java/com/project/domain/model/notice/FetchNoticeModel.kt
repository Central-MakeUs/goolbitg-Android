package com.project.domain.model.notice

data class FetchNoticesModel(
    val totalSize: Int,
    val totalPages: Int,
    val size: Int,
    val page: Int,
    val items: List<FetchNoticesItemModel>,
)

data class FetchNoticesItemModel(
    val id: Int,
    val receiverId: String,
    val message: String,
    val publishDateTime: String,
    val type: String,
    val read: Boolean,
)
