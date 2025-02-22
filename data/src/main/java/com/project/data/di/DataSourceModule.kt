package com.project.data.di

import com.project.data.remote.datasource.AuthDataSource
import com.project.data.remote.datasource.BuyOrNotDataSource
import com.project.data.remote.datasource.ChallengeDataSource
import com.project.data.remote.datasource.UserDataSource
import com.project.data.remote.datasource.UtilDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataSourceModule {
    @Provides
    @Singleton
    fun provideAuthDataSource(retrofit: Retrofit): AuthDataSource = retrofit.create(AuthDataSource::class.java)

    @Provides
    @Singleton
    fun provideUserDataSource(retrofit: Retrofit): UserDataSource = retrofit.create(UserDataSource::class.java)

    @Provides
    @Singleton
    fun provideChallengeDataSource(retrofit: Retrofit): ChallengeDataSource = retrofit.create(ChallengeDataSource::class.java)

    @Provides
    @Singleton
    fun provideBuyOrNotDataSource(retrofit: Retrofit): BuyOrNotDataSource = retrofit.create(BuyOrNotDataSource::class.java)

    @Provides
    @Singleton
    fun provideUtilDataSource(retrofit: Retrofit): UtilDataSource = retrofit.create(UtilDataSource::class.java)
}
