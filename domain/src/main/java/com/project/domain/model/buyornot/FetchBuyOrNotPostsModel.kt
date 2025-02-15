package com.project.domain.model.buyornot

data class FetchBuyOrNotPostsModel(
    val totalSize: Int,
    val totalPages: Int,
    val size: Int,
    val page: Int,
    val items: BuyOrNotPostingModel
)
