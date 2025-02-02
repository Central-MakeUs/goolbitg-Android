package com.project.data.remote.datasource

import com.project.data.remote.request.auth.LoginReq
import com.project.data.remote.request.auth.RefreshTokenReq
import com.project.data.remote.request.auth.RegisterReq
import com.project.data.remote.response.auth.LoginRes
import com.project.data.remote.response.auth.RefreshTokenRes
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthDataSource {
    /** 회원가입 */
    @POST("/v1/auth/register")
    suspend fun register(
        @Body body: RegisterReq
    ): Response<Unit>

    /** 로그인 요청 */
    @POST("/v1/auth/login")
    suspend fun login(
        @Body body: LoginReq
    ): Response<LoginRes>

    /** 토큰 재발급 */
    @POST("/v1/auth/refresh")
    suspend fun refreshToken(
        @Body body: RefreshTokenReq,
    ): Response<RefreshTokenRes>

    /** 로그아웃 */
    @POST("/v1/auth/logout")
    suspend fun logout(): Response<Unit>

    /** 회원 탈퇴 */
    @POST("/v1/auth/unregister")
    suspend fun withdrawAccount(): Response<Unit>
}
