package com.project.data.repository

import com.project.data.remote.common.NetworkUtils
import com.project.data.remote.datasource.AuthDataSource
import com.project.data.remote.request.auth.LoginReq
import com.project.data.remote.request.auth.RefreshTokenReq
import com.project.data.remote.request.auth.RegisterReq
import com.project.data.remote.request.auth.WithdrawReq
import com.project.domain.model.DataState
import com.project.domain.model.auth.LoginModel
import com.project.domain.model.auth.RefreshTokenModel
import com.project.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authDataSource: AuthDataSource
) : AuthRepository {
    override suspend fun register(type: String, idToken: String): Flow<DataState<Boolean>> {
        return NetworkUtils.handleApi(
            execute = {
                authDataSource.register(
                    body = RegisterReq(
                        type = type,
                        idToken = idToken
                    )
                )
            },
            mapper = { true }
        )
    }

    override suspend fun login(type: String, idToken: String): Flow<DataState<LoginModel>> {
        return NetworkUtils.handleApi(
            execute = { authDataSource.login(body = LoginReq(type = type, idToken = idToken)) },
            mapper = { it?.toDomainModel() }
        )
    }

    override suspend fun refreshToken(refreshToken: String): Flow<DataState<RefreshTokenModel>> {
        return NetworkUtils.handleApi(
            execute = { authDataSource.refreshToken(body = RefreshTokenReq(refreshToken = refreshToken)) },
            mapper = { it?.toDomainModel() }
        )
    }

    override suspend fun logout(): Flow<DataState<Boolean>> {
        return NetworkUtils.handleApi(
            execute = { authDataSource.logout() },
            mapper = { true }
        )
    }

    override suspend fun withdrawAccount(reason: String): Flow<DataState<Boolean>> {
        return NetworkUtils.handleApi(
            execute = { authDataSource.withdrawAccount(body = WithdrawReq(reason = reason)) },
            mapper = { true }
        )
    }
}
