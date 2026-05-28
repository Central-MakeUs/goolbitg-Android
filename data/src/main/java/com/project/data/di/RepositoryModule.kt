package com.project.data.di

import com.project.data.remote.datasource.AnalysisDataSource
import com.project.data.remote.datasource.AuthDataSource
import com.project.data.remote.datasource.BuyOrNotDataSource
import com.project.data.remote.datasource.ChallengeDataSource
import com.project.data.remote.datasource.ChallengeGroupDataSource
import com.project.data.remote.datasource.NoticeDataSource
import com.project.data.remote.datasource.UserDataSource
import com.project.data.remote.datasource.UtilDataSource
import com.project.data.repository.AnalysisRepositoryImpl
import com.project.data.repository.AuthRepositoryImpl
import com.project.data.repository.BuyOrNotRepositoryImpl
import com.project.data.repository.ChallengeGroupRepositoryImpl
import com.project.data.repository.ChallengeRepositoryImpl
import com.project.data.repository.NoticeRepositoryImpl
import com.project.data.repository.UserRepositoryImpl
import com.project.domain.repository.AnalysisRepository
import com.project.domain.repository.AuthRepository
import com.project.domain.repository.BuyOrNotRepository
import com.project.domain.repository.ChallengeGroupRepository
import com.project.domain.repository.ChallengeRepository
import com.project.domain.repository.NoticeRepository
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
    fun provideBuyOrNotRepository(
        buyOrNotDataSource: BuyOrNotDataSource,
        utilDataSource: UtilDataSource,
        stompChatClient: com.project.data.remote.socket.StompChatClient,
        chatMessageDao: com.project.data.local.db.ChatMessageDao
    ): BuyOrNotRepository {
        return BuyOrNotRepositoryImpl(
            buyOrNotDataSource = buyOrNotDataSource,
            utilDataSource = utilDataSource,
            stompChatClient = stompChatClient,
            chatMessageDao = chatMessageDao
        )
    }

    @Provides
    @Singleton
    fun provideNoticeRepository(
        noticeDataSource: NoticeDataSource
    ): NoticeRepository {
        return NoticeRepositoryImpl(noticeDataSource = noticeDataSource)
    }

    @Provides
    @Singleton
    fun provideChallengeGroupRepository(
        challengeGroupDataSource: ChallengeGroupDataSource
    ): ChallengeGroupRepository {
        return ChallengeGroupRepositoryImpl(dataSource = challengeGroupDataSource)
    }

    @Provides
    @Singleton
    fun provideAnalysisRepository(
        analysisDataSource: AnalysisDataSource
    ): AnalysisRepository {
        return AnalysisRepositoryImpl(dataSource = analysisDataSource)
    }
}
