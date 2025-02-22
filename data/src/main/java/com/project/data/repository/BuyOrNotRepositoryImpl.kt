package com.project.data.repository

import com.project.data.remote.common.NetworkUtils
import com.project.data.remote.datasource.BuyOrNotDataSource
import com.project.data.remote.datasource.UtilDataSource
import com.project.data.remote.request.buyornot.ReportPostingReq
import com.project.data.remote.request.buyornot.UpsertBuyOrNotPostingReq
import com.project.data.remote.request.buyornot.VotePostingReq
import com.project.domain.model.DataState
import com.project.domain.model.buyornot.BuyOrNotPostingModel
import com.project.domain.model.buyornot.FetchBuyOrNotPostsModel
import com.project.domain.model.buyornot.VotePostingModel
import com.project.domain.repository.BuyOrNotRepository
import kotlinx.coroutines.flow.Flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

class BuyOrNotRepositoryImpl @Inject constructor(
    private val buyOrNotDataSource: BuyOrNotDataSource,
    private val utilDataSource: UtilDataSource
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

    override suspend fun reportPosting(postId: Int, reason: String): Flow<DataState<Boolean>> {
        return NetworkUtils.handleApi(
            execute = {
                buyOrNotDataSource.reportPosting(postId = postId, body = ReportPostingReq(reason = reason))
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

    override suspend fun uploadImage(byteArray: ByteArray): Flow<DataState<String>> {
        return NetworkUtils.handleApi(
            execute = {
                val requestBody = byteArray.toRequestBody("image/jpeg".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("image", "Image", requestBody)
                utilDataSource.uploadImg(
                    image = body
                )
            },
            mapper = { it?.url }
        )
    }
}
