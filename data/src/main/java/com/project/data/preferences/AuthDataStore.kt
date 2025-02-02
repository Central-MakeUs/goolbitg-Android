package com.project.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.project.data.preferences.DataStoreKeys.ACCESS_TOKEN
import com.project.data.preferences.DataStoreKeys.IS_PERMISSION_FLOW
import com.project.data.preferences.DataStoreKeys.REFRESH_TOKEN
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.datastore by preferencesDataStore("auth")

class AuthDataStore
@Inject
constructor(
    private val context: Context,
) {
    fun getAccessToken(): Flow<String?> {
        return context.datastore.data.map {
            it[ACCESS_TOKEN]
        }
    }

    fun getRefreshToken(): Flow<String?> {
        return context.datastore.data.map {
            it[REFRESH_TOKEN]
        }
    }

    fun getIsPermissionFlow(): Flow<Boolean> {
        return context.datastore.data.map {
            it[IS_PERMISSION_FLOW] ?: true
        }
    }

    suspend fun setAccessToken(accessToken: String) {
        context.datastore.edit { preferences ->
            preferences[ACCESS_TOKEN] = accessToken
        }
    }

    suspend fun setRefreshToken(refreshToken: String) {
        context.datastore.edit { preferences ->
            preferences[REFRESH_TOKEN] = refreshToken
        }
    }

    suspend fun setIsPermissionFlow(isPermissionFlow: Boolean) {
        context.datastore.edit { preferences ->
            preferences[IS_PERMISSION_FLOW] = isPermissionFlow
        }
    }
}
