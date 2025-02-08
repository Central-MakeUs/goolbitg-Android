package com.project.data.remote.interceptor

import com.project.data.preferences.AuthDataStore
import com.project.data.remote.datasource.AuthDataSource
import com.project.data.remote.request.auth.RefreshTokenReq
import dagger.Lazy
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Singleton

@Singleton
class TokenInterceptor(
    private val authDataStore: AuthDataStore,
    private val authDataSource: Lazy<AuthDataSource>,
) : Interceptor {
    @Volatile
    private var isRefreshing = false
    private val lock = Any()

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val pathSuffix = request.url.encodedPath

        // refreshToken 요청인지 확인 (URL 경로로 판단)
        if (pathSuffix.contains("auth/login")
            || pathSuffix.contains("auth/register")
            || pathSuffix.contains("auth/refresh")
        ) {
            return chain.proceed(request)
        }

        val token =
            runBlocking {
                authDataStore.getAccessToken().firstOrNull()
            }
        val newRequest =
            chain.request().newBuilder()
                .apply {
                    if (token != null) {
                        addHeader("Authorization", "Bearer $token")
                    }
                }.build()

        val response = chain.proceed(newRequest)

        // 토큰 만료 시 처리
        if (response.code == 401) {
            if (!isRefreshing) {
                isRefreshing = true
                try {
                    val newTokenPair = refreshAccessToken()
                    if (newTokenPair != null) {
                        runBlocking {
                            authDataStore.setAccessToken(newTokenPair.first)
                            authDataStore.setRefreshToken(newTokenPair.second)
                        }
                        response.close() // 기존 응답 닫기
                        val newRequest =
                            newRequest.newBuilder()
                                .header("Authorization", "Bearer ${newTokenPair.first}")
                                .build()
                        return chain.proceed(newRequest) // 재시도
                    }
                } finally {
                    isRefreshing = false
                }
            }
        }
        return response
    }

    /**
     * RefreshToken을 사용하여 새로운 AccessToken을 발급받는 함수
     */
    private fun refreshAccessToken(): Pair<String, String>? {
        return try {
            val refreshToken = runBlocking { authDataStore.getRefreshToken().firstOrNull() }
            if (refreshToken != null) {
                val response = runBlocking {
                    authDataSource.get().refreshToken(body = RefreshTokenReq(refreshToken = refreshToken))
                }
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        Pair(body.accessToken, body.refreshToken)
                    } else {
                        null
                    }
                } else {
                    null
                }
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}
