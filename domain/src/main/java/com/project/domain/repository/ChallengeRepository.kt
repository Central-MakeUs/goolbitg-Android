package com.project.domain.repository

import com.project.domain.model.DataState
import com.project.domain.model.challenge.ChallengeInfoModel
import com.project.domain.model.challenge.ChallengeListModel
import com.project.domain.model.challenge.ChallengeRecordListModel
import com.project.domain.model.challenge.ChallengeRecordModel
import com.project.domain.model.challenge.ChallengeStatModel
import com.project.domain.model.challenge.ChallengeTripleModel
import kotlinx.coroutines.flow.Flow

interface ChallengeRepository {
    suspend fun fetchChallengeList(
        page: Int = 0,
        size: Int = 10,
        spendingTypeId: Int? = null
    ): Flow<DataState<ChallengeListModel>>

    suspend fun fetchChallengeInfo(challengeId: Int): Flow<DataState<ChallengeInfoModel>>

    suspend fun enrollChallenge(challengeId: Int): Flow<DataState<Boolean>>

    suspend fun fetchEnrolledChallengeList(
        page: Int = 0,
        size: Int = 10,
        date: String? = null,
        status: String? = null
    ): Flow<DataState<ChallengeRecordListModel>>

    suspend fun fetchEnrolledChallengeInfo(
        challengeId: Int,
        date: String? = null
    ): Flow<DataState<ChallengeRecordModel>>

    suspend fun deleteChallenge(challengeId: Int): Flow<DataState<Boolean>>

    suspend fun completeTodayChallengeRecord(challengeId: Int): Flow<DataState<ChallengeRecordModel>>

    suspend fun fetchChallengeStat(challengeId: Int): Flow<DataState<ChallengeStatModel>>

    suspend fun fetchChallengeTriple(challengeId: Int): Flow<DataState<ChallengeTripleModel>>
}