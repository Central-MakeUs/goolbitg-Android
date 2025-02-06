package com.project.data.remote.datasource

import com.project.data.remote.response.challenge.ChallengeInfoRes
import com.project.data.remote.response.challenge.ChallengeListRes
import com.project.data.remote.response.challenge.ChallengeRecordListRes
import com.project.data.remote.response.challenge.ChallengeRecordRes
import com.project.data.remote.response.challenge.ChallengeStatRes
import com.project.data.remote.response.challenge.ChallengeTripleRes
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ChallengeDataSource {
    @GET("/v1/challenges")
    suspend fun fetchChallengeList(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10,
        @Query("spendingTypeId") spendingTypeId: Int? = null
    ): Response<ChallengeListRes>

    @GET("/v1/challenges/{challengeId}")
    suspend fun fetchChallengeInfo(@Path("challengeId") challengeId: Int): Response<ChallengeInfoRes>

    @POST("/v1/challenges/{challengeId}/enroll")
    suspend fun enrollChallenge(@Path("challengeId") challengeId: Int): Response<Unit>

    @GET("/v1/challengeRecords")
    suspend fun fetchEnrolledChallengeList(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10,
        @Query("date") date: String? = null,
        @Query("status") status: String? = null
    ): Response<ChallengeRecordListRes>

    @GET("/v1/challengeRecords/{challengeId}")
    suspend fun fetchEnrolledChallengeInfo(
        @Path("challengeId") challengeId: Int,
        @Query("date") date: String? = null
    ): Response<ChallengeRecordRes>

    @DELETE("/v1/challengeRecords/{challengeId}")
    suspend fun deleteChallenge(
        @Path("challengeId") challengeId: Int,
    ): Response<Unit>

    @POST("/v1/challengeRecords/{challengeId}/check")
    suspend fun completeTodayChallengeRecord(
        @Path("challengeId") challengeId: Int,
    ): Response<ChallengeRecordRes>

    @GET("/v1/challengeStat/{challengeId}")
    suspend fun fetchChallengeStat(
        @Path("challengeId") challengeId: Int,
    ): Response<ChallengeStatRes>

    @GET("/v1/challengeTripple/{challengeId}")
    suspend fun fetchChallengeTriple(
        @Path("challengeId") challengeId: Int,
    ): Response<ChallengeTripleRes>
}