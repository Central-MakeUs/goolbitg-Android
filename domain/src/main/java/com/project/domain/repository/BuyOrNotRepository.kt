package com.project.domain.repository

import com.project.domain.model.DataState
import com.project.domain.model.buyornot.BuyOrNotPostingModel
import com.project.domain.model.buyornot.FetchBuyOrNotPostsModel
import com.project.domain.model.buyornot.VotePostingModel
import kotlinx.coroutines.flow.Flow

interface BuyOrNotRepository {
    suspend fun fetchBuyOrNotPosts(
        page: Int,
        size: Int,
        isCreated: Boolean
    ): Flow<DataState<FetchBuyOrNotPostsModel>>

    suspend fun postBuyOrNotPosting(
        productName: String,
        productPrice: Int,
        productImageUrl: String,
        goodReason: String,
        badReason: String,
    ): Flow<DataState<BuyOrNotPostingModel>>

    suspend fun modifyBuyOrNotPosting(
        postId: Int,
        productName: String,
        productPrice: Int,
        productImageUrl: String,
        goodReason: String,
        badReason: String,
    ): Flow<DataState<BuyOrNotPostingModel>>

    suspend fun getBuyOrNotPostingDetail(postId: Int): Flow<DataState<BuyOrNotPostingModel>>
    suspend fun deleteBuyOrNotPosting(postId: Int): Flow<DataState<Boolean>>
    suspend fun votePosting(postId: Int, vote: String): Flow<DataState<VotePostingModel>>
}
