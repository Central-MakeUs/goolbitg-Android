package com.project.data.di

import android.content.Context
import androidx.room.Room
import com.project.data.local.db.ChatMessageDao
import com.project.data.local.db.GoolbitgDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideGoolbitgDatabase(
        @ApplicationContext context: Context
    ): GoolbitgDatabase = Room
        .databaseBuilder(context, GoolbitgDatabase::class.java, "goolbitg.db")
        .fallbackToDestructiveMigration() // 스키마 변경 시 데이터 폐기 (개발 단계)
        .build()

    @Provides
    @Singleton
    fun provideChatMessageDao(db: GoolbitgDatabase): ChatMessageDao = db.chatMessageDao()
}
