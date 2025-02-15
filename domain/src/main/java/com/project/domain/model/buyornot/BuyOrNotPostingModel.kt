package com.project.domain.model.buyornot

data class BuyOrNotPostingModel(
    val id: Int,
    val writerId: String,
    val productName: String,
    val productPrice: Int,
    val productImageUrl: String,
    val goodReason: String,
    val badReason: String,
    val goodVoteCount: Int,
    val badVoteCount: Int,
)
