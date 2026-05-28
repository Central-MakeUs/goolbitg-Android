package com.project.data.remote.datasource

import com.project.data.remote.request.challengegroup.ChallengeGroupEnrollReq
import com.project.data.remote.request.challengegroup.ChallengeGroupReq
import com.project.data.remote.response.challengegroup.ChallengeGroupListRes
import com.project.data.remote.response.challengegroup.ChallengeGroupRankRes
import com.project.data.remote.response.challengegroup.ChallengeGroupRecordRes
import com.project.data.remote.response.challengegroup.ChallengeGroupRes
import com.project.data.remote.response.challengegroup.ChallengeGroupTrippleRes
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ChallengeGroupDataSource {
    @GET("/api/v1/challengeGroups")
    suspend fun fetchChallengeGroupList(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("search") search: String?,
        @Query("created") created: Boolean?,
        @Query("participating") participating: Boolean?
    ): Response<ChallengeGroupListRes>

    @POST("/api/v1/challengeGroups")
    suspend fun createChallengeGroup(
        @Body body: ChallengeGroupReq
    ): Response<ChallengeGroupRes>

    @GET("/api/v1/challengeGroups/{groupId}")
    suspend fun fetchChallengeGroupDetail(
        @Path("groupId") groupId: Int
    ): Response<ChallengeGroupRankRes>

    @PUT("/api/v1/challengeGroups/{groupId}")
    suspend fun updateChallengeGroup(
        @Path("groupId") groupId: Int,
        @Body body: ChallengeGroupReq
    ): Response<ChallengeGroupRes>

    @DELETE("/api/v1/challengeGroups/{groupId}")
    suspend fun deleteChallengeGroup(
        @Path("groupId") groupId: Int
    ): Response<Unit>

    @POST("/api/v1/challengeGroups/{groupId}/enroll")
    suspend fun enrollChallengeGroup(
        @Path("groupId") groupId: Int,
        @Body body: ChallengeGroupEnrollReq
    ): Response<Unit>

    @POST("/api/v1/challengeGroups/{groupId}/check")
    suspend fun checkChallengeGroup(
        @Path("groupId") groupId: Int
    ): Response<ChallengeGroupRecordRes>

    @GET("/api/v1/challengeGroups/{groupId}/tripple")
    suspend fun fetchChallengeGroupTripple(
        @Path("groupId") groupId: Int
    ): Response<ChallengeGroupTrippleRes>

    @POST("/api/v1/challengeGroups/{groupId}/exit")
    suspend fun exitChallengeGroup(
        @Path("groupId") groupId: Int
    ): Response<Unit>
}
