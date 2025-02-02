package com.project.domain.repository

import com.project.domain.model.DataState
import com.project.domain.model.auth.LoginModel
import com.project.domain.model.auth.RefreshTokenModel
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun register(type: String, idToken: String): Flow<DataState<Boolean>>
    suspend fun login(type: String, idToken: String): Flow<DataState<LoginModel>>
    suspend fun refreshToken(refreshToken: String): Flow<DataState<RefreshTokenModel>>
    suspend fun logout(): Flow<DataState<Boolean>>
    suspend fun withdrawAccount(): Flow<DataState<Boolean>>
}
