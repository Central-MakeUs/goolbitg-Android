package com.project.data.remote.datasource

import com.project.data.remote.response.notice.FetchNoticesRes
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface NoticeDataSource {
    @GET("/v1/notices")
    suspend fun fetchNotices(
        @Query("pages") pages: Int,
        @Query("size") size: Int,
        @Query("type") type: String
    ): Response<FetchNoticesRes>

    @POST("/v1/notices/{noticeId}")
    suspend fun postReadPost(
        @Path("noticeId") noticeId: Int
    ): Response<Unit>
}
