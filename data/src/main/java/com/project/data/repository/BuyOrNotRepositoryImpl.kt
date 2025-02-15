package com.project.data.repository

import com.project.data.remote.common.NetworkUtils
import com.project.data.remote.datasource.BuyOrNotDataSource
import com.project.data.remote.request.buyornot.UpsertBuyOrNotPostingReq
import com.project.data.remote.request.buyornot.VotePostingReq
import com.project.domain.model.DataState
import com.project.domain.model.buyornot.BuyOrNotPostingModel
import com.project.domain.model.buyornot.FetchBuyOrNotPostsModel
import com.project.domain.model.buyornot.VotePostingModel
import com.project.domain.repository.BuyOrNotRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class BuyOrNotRepositoryImpl @Inject constructor(
    private val buyOrNotDataSource: BuyOrNotDataSource
) : BuyOrNotRepository {
    override suspend fun fetchBuyOrNotPosts(
        page: Int,
        size: Int,
        isCreated: Boolean
    ): Flow<DataState<FetchBuyOrNotPostsModel>> {
        return NetworkUtils.handleApi(
            execute = {
                buyOrNotDataSource.fetchBuyOrNotPosts(
                    page = page,
                    size = size,
                    isCreated = isCreated
                )
            },
            mapper = { it?.toDomainModel() }
        )
    }

    override suspend fun postBuyOrNotPosting(
        productName: String,
        productPrice: Int,
        productImageUrl: String,
        goodReason: String,
        badReason: String
    ): Flow<DataState<BuyOrNotPostingModel>> {
        return NetworkUtils.handleApi(
            execute = {
                buyOrNotDataSource.postBuyOrNotPosting(
                    body = UpsertBuyOrNotPostingReq(
                        productName = productName,
                        productPrice = productPrice,
                        productImageUrl = productImageUrl,
                        goodReason = goodReason,
                        badReason = badReason,
                    )
                )
            },
            mapper = { it?.toDomainModel() }
        )

    }

    override suspend fun modifyBuyOrNotPosting(
        postId: Int,
        productName: String,
        productPrice: Int,
        productImageUrl: String,
        goodReason: String,
        badReason: String
    ): Flow<DataState<BuyOrNotPostingModel>> {
        return NetworkUtils.handleApi(
            execute = {
                buyOrNotDataSource.modifyBuyOrNotPosting(
                    postId = postId,
                    body = UpsertBuyOrNotPostingReq(
                        productName = productName,
                        productPrice = productPrice,
                        productImageUrl = productImageUrl,
                        goodReason = goodReason,
                        badReason = badReason,
                    )
                )
            },
            mapper = { it?.toDomainModel() }
        )

    }

    override suspend fun getBuyOrNotPostingDetail(postId: Int): Flow<DataState<BuyOrNotPostingModel>> {
        return NetworkUtils.handleApi(
            execute = {
                buyOrNotDataSource.getBuyOrNotPostingDetail(postId = postId)
            },
            mapper = { it?.toDomainModel() }
        )

    }

    override suspend fun deleteBuyOrNotPosting(postId: Int): Flow<DataState<Boolean>> {
        return NetworkUtils.handleApi(
            execute = {
                buyOrNotDataSource.deleteBuyOrNotPosting(postId = postId)
            },
            mapper = { true }
        )

    }

    override suspend fun votePosting(postId: Int, vote: String): Flow<DataState<VotePostingModel>> {
        return NetworkUtils.handleApi(
            execute = {
                buyOrNotDataSource.votePosting(
                    postId = postId,
                    body = VotePostingReq(vote = vote)
                )
            },
            mapper = { it?.toDomainModel() }
        )
    }
}
