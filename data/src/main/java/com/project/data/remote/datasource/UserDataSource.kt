package com.project.data.remote.datasource

import com.project.data.remote.request.user.CheckNicknameDuplicatedReq
import com.project.data.remote.request.user.UserAgreementReq
import com.project.data.remote.request.user.UserCheckListReq
import com.project.data.remote.request.user.UserHabitReq
import com.project.data.remote.request.user.UserInfoReq
import com.project.data.remote.request.user.UserPatternReq
import com.project.data.remote.response.user.CheckNicknameDuplicatedRes
import com.project.data.remote.response.user.CheckRegisterStatusRes
import com.project.data.remote.response.user.GetUserInfoRes
import com.project.data.remote.response.user.GetWeeklyRecordStatusRes
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface UserDataSource {
    @GET("/v1/users/me")
    suspend fun getRemoteUserInfo(): Response<GetUserInfoRes>

    @GET("/v1/users/me/registerStatus")
    suspend fun checkRegisterStatus(): Response<CheckRegisterStatusRes>

    @GET("/v1/users/me/weeklyStatus")
    suspend fun getWeeklyRecordStatus(@Query("date") date: String? = null): Response<GetWeeklyRecordStatusRes>

    @POST("/v1/users/nickname/check")
    suspend fun checkNicknameDuplicated(@Body body: CheckNicknameDuplicatedReq): Response<CheckNicknameDuplicatedRes>

    @POST("/v1/users/me/agreePushNotification")
    suspend fun agreePushNotification(): Response<Unit>

    @POST("/v1/users/me/agreement")
    suspend fun setRemoteUserAgreement(@Body body: UserAgreementReq): Response<Unit>

    @POST("/v1/users/me/info")
    suspend fun setRemoteUserInfo(@Body body: UserInfoReq): Response<Unit>

    @POST("/v1/users/me/checklist")
    suspend fun setRemoteUserCheckList(@Body body: UserCheckListReq): Response<Unit>

    @POST("/v1/users/me/habit")
    suspend fun setRemoteUserHabit(@Body body: UserHabitReq): Response<Unit>

    @POST("/v1/users/me/pattern")
    suspend fun setRemoteUserPattern(@Body body: UserPatternReq): Response<Unit>
}
