package com.project.data.remote.datasource

import com.project.data.remote.request.buyornot.ReportPostingReq
import com.project.data.remote.request.buyornot.UpsertBuyOrNotPostingReq
import com.project.data.remote.request.buyornot.VotePostingReq
import com.project.data.remote.response.buyornot.FetchBuyOrNotPostsRes
import com.project.data.remote.response.buyornot.BuyOrNotPostingRes
import com.project.data.remote.response.buyornot.VotePostingRes
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface BuyOrNotDataSource {
    @GET("/v1/buyOrNots")
    suspend fun fetchBuyOrNotPosts(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("created") isCreated: Boolean
    ): Response<FetchBuyOrNotPostsRes>

    @POST("/v1/buyOrNots")
    suspend fun postBuyOrNotPosting(
        @Body body: UpsertBuyOrNotPostingReq
    ): Response<BuyOrNotPostingRes>

    @PUT("/v1/buyOrNots/{postId}")
    suspend fun modifyBuyOrNotPosting(
        @Path("postId") postId: Int,
        @Body body: UpsertBuyOrNotPostingReq
    ): Response<BuyOrNotPostingRes>

    @GET("/v1/buyOrNots/{postId}")
    suspend fun getBuyOrNotPostingDetail(
        @Path("postId") postId: Int,
    ): Response<BuyOrNotPostingRes>

    @DELETE("/v1/buyOrNots/{postId}")
    suspend fun deleteBuyOrNotPosting(
        @Path("postId") postId: Int,
    ): Response<Unit>

    @POST("/v1/buyOrNots/{postId}/report")
    suspend fun reportPosting(
        @Path("postId") postId: Int,
        @Body body: ReportPostingReq
    ): Response<Unit>

    @POST("/v1/buyOrNots/{postId}/vote")
    suspend fun votePosting(
        @Path("postId") postId: Int,
        @Body body: VotePostingReq
    ): Response<VotePostingRes>
}
