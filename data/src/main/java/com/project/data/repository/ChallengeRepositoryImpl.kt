package com.project.data.repository

import com.project.data.remote.common.NetworkUtils
import com.project.data.remote.datasource.ChallengeDataSource
import com.project.domain.model.DataState
import com.project.domain.model.challenge.ChallengeInfoModel
import com.project.domain.model.challenge.ChallengeListModel
import com.project.domain.model.challenge.ChallengeRecordListModel
import com.project.domain.model.challenge.ChallengeRecordModel
import com.project.domain.model.challenge.ChallengeStatModel
import com.project.domain.model.challenge.ChallengeTripleModel
import com.project.domain.repository.ChallengeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ChallengeRepositoryImpl @Inject constructor(
    private val challengeDataSource: ChallengeDataSource
): ChallengeRepository {
    override suspend fun fetchChallengeList(
        page: Int,
        size: Int,
        spendingTypeId: Int?
    ): Flow<DataState<ChallengeListModel>> {
        return NetworkUtils.handleApi(
            execute = { challengeDataSource.fetchChallengeList(
                page = page,
                size = size,
                spendingTypeId = spendingTypeId
            ) },
            mapper = { it?.toDomainModel() }
        )
    }

    override suspend fun fetchChallengeInfo(challengeId: Int): Flow<DataState<ChallengeInfoModel>> {
        return NetworkUtils.handleApi(
            execute = { challengeDataSource.fetchChallengeInfo(
                challengeId = challengeId
            ) },
            mapper = { it?.toDomainModel() }
        )
    }

    override suspend fun enrollChallenge(challengeId: Int): Flow<DataState<Boolean>> {
        return NetworkUtils.handleApi(
            execute = { challengeDataSource.enrollChallenge(
                challengeId = challengeId
            ) },
            mapper = { true }
        )
    }

    override suspend fun fetchEnrolledChallengeList(
        page: Int,
        size: Int,
        date: String?,
        status: String?
    ): Flow<DataState<ChallengeRecordListModel>> {
        return NetworkUtils.handleApi(
            execute = { challengeDataSource.fetchEnrolledChallengeList(
                page = page,
                size = size,
                date = date,
                status = status
            ) },
            mapper = { it?.toDomainModel() }
        )
    }

    override suspend fun fetchEnrolledChallengeInfo(
        challengeId: Int,
        date: String?
    ): Flow<DataState<ChallengeRecordModel>> {
        return NetworkUtils.handleApi(
            execute = { challengeDataSource.fetchEnrolledChallengeInfo(
                challengeId = challengeId,
                date = date
            ) },
            mapper = { it?.toDomainModel() }
        )
    }

    override suspend fun deleteChallenge(challengeId: Int): Flow<DataState<Boolean>> {
        return NetworkUtils.handleApi(
            execute = { challengeDataSource.deleteChallenge(
                challengeId = challengeId,
            ) },
            mapper = { true }
        )
    }

    override suspend fun completeTodayChallengeRecord(challengeId: Int): Flow<DataState<ChallengeRecordModel>> {
        return NetworkUtils.handleApi(
            execute = { challengeDataSource.completeTodayChallengeRecord(
                challengeId = challengeId,
            ) },
            mapper = { it?.toDomainModel() }
        )
    }

    override suspend fun fetchChallengeStat(challengeId: Int): Flow<DataState<ChallengeStatModel>> {
        return NetworkUtils.handleApi(
            execute = { challengeDataSource.fetchChallengeStat(
                challengeId = challengeId,
            ) },
            mapper = { it?.toDomainModel() }
        )
    }

    override suspend fun fetchChallengeTriple(challengeId: Int): Flow<DataState<ChallengeTripleModel>> {
        return NetworkUtils.handleApi(
            execute = { challengeDataSource.fetchChallengeTriple(
                challengeId = challengeId,
            ) },
            mapper = { it?.toDomainModel() }
        )
    }

}