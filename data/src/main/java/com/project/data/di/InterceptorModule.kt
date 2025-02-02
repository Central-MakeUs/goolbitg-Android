package com.project.data.di

import com.project.data.preferences.AuthDataStore
import com.project.data.remote.datasource.AuthDataSource
import com.project.data.remote.interceptor.TokenInterceptor
import dagger.Lazy
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object InterceptorModule {
    @Provides
    @Singleton
    fun provideTokenInterceptor(
        authDataStore: AuthDataStore,
        authDataSource: Lazy<AuthDataSource>,
    ): TokenInterceptor {
        return TokenInterceptor(authDataStore, authDataSource)
    }
}
