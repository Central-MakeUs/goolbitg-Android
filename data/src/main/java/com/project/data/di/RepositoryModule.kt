package com.project.data.di

import com.project.data.remote.datasource.AuthDataSource
import com.project.data.remote.datasource.BuyOrNotDataSource
import com.project.data.remote.datasource.ChallengeDataSource
import com.project.data.remote.datasource.UserDataSource
import com.project.data.repository.AuthRepositoryImpl
import com.project.data.repository.BuyOrNotRepositoryImpl
import com.project.data.repository.ChallengeRepositoryImpl
import com.project.data.repository.UserRepositoryImpl
import com.project.domain.repository.AuthRepository
import com.project.domain.repository.BuyOrNotRepository
import com.project.domain.repository.ChallengeRepository
import com.project.domain.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun provideAuthRepository(authDataSource: AuthDataSource): AuthRepository {
        return AuthRepositoryImpl(authDataSource = authDataSource)
    }

    @Provides
    @Singleton
    fun provideUserRepository(userDataSource: UserDataSource): UserRepository {
        return UserRepositoryImpl(userDataSource = userDataSource)
    }

    @Provides
    @Singleton
    fun provideChallengeRepository(challengeDataSource: ChallengeDataSource): ChallengeRepository {
        return ChallengeRepositoryImpl(challengeDataSource = challengeDataSource)
    }

    @Provides
    @Singleton
    fun provideBuyOrNotRepository(buyOrNotDataSource: BuyOrNotDataSource): BuyOrNotRepository {
        return BuyOrNotRepositoryImpl(buyOrNotDataSource = buyOrNotDataSource)
    }
}
