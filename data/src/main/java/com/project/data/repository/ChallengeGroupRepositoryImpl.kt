package com.project.data.repository

import com.project.data.remote.common.NetworkUtils
import com.project.data.remote.datasource.ChallengeGroupDataSource
import com.project.data.remote.request.challengegroup.ChallengeGroupEnrollReq
import com.project.data.remote.request.challengegroup.ChallengeGroupReq
import com.project.domain.model.DataState
import com.project.domain.model.challengegroup.ChallengeGroupListModel
import com.project.domain.model.challengegroup.ChallengeGroupModel
import com.project.domain.model.challengegroup.ChallengeGroupRankModel
import com.project.domain.model.challengegroup.ChallengeGroupRecordModel
import com.project.domain.model.challengegroup.ChallengeGroupTrippleModel
import com.project.domain.repository.ChallengeGroupRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ChallengeGroupRepositoryImpl @Inject constructor(
    private val dataSource: ChallengeGroupDataSource
) : ChallengeGroupRepository {

    override suspend fun fetchChallengeGroupList(
        page: Int,
        size: Int,
        search: String?,
        created: Boolean?,
        participating: Boolean?
    ): Flow<DataState<ChallengeGroupListModel>> = NetworkUtils.handleApi(
        execute = { dataSource.fetchChallengeGroupList(page, size, search, created, participating) },
        mapper = { it?.toDomainModel() }
    )

    override suspend fun createChallengeGroup(
        title: String,
        reward: Int,
        hashtags: List<String>,
        category: String,
        maxSize: Int,
        isHidden: Boolean,
        password: String?
    ): Flow<DataState<ChallengeGroupModel>> = NetworkUtils.handleApi(
        execute = {
            dataSource.createChallengeGroup(
                ChallengeGroupReq(title, reward, hashtags, category, maxSize, isHidden, password)
            )
        },
        mapper = { it?.toDomainModel() }
    )

    override suspend fun fetchChallengeGroupDetail(groupId: Int): Flow<DataState<ChallengeGroupRankModel>> =
        NetworkUtils.handleApi(
            execute = { dataSource.fetchChallengeGroupDetail(groupId) },
            mapper = { it?.toDomainModel() }
        )

    override suspend fun updateChallengeGroup(
        groupId: Int,
        title: String,
        reward: Int,
        hashtags: List<String>,
        category: String,
        maxSize: Int,
        isHidden: Boolean,
        password: String?
    ): Flow<DataState<ChallengeGroupModel>> = NetworkUtils.handleApi(
        execute = {
            dataSource.updateChallengeGroup(
                groupId,
                ChallengeGroupReq(title, reward, hashtags, category, maxSize, isHidden, password)
            )
        },
        mapper = { it?.toDomainModel() }
    )

    override suspend fun deleteChallengeGroup(groupId: Int): Flow<DataState<Boolean>> =
        NetworkUtils.handleApi(
            execute = { dataSource.deleteChallengeGroup(groupId) },
            mapper = { true }
        )

    override suspend fun enrollChallengeGroup(groupId: Int, password: String?): Flow<DataState<Boolean>> =
        NetworkUtils.handleApi(
            execute = { dataSource.enrollChallengeGroup(groupId, ChallengeGroupEnrollReq(password)) },
            mapper = { true }
        )

    override suspend fun checkChallengeGroup(groupId: Int): Flow<DataState<ChallengeGroupRecordModel>> =
        NetworkUtils.handleApi(
            execute = { dataSource.checkChallengeGroup(groupId) },
            mapper = { it?.toDomainModel() }
        )

    override suspend fun fetchChallengeGroupTripple(groupId: Int): Flow<DataState<ChallengeGroupTrippleModel>> =
        NetworkUtils.handleApi(
            execute = { dataSource.fetchChallengeGroupTripple(groupId) },
            mapper = { it?.toDomainModel() }
        )

    override suspend fun exitChallengeGroup(groupId: Int): Flow<DataState<Boolean>> =
        NetworkUtils.handleApi(
            execute = { dataSource.exitChallengeGroup(groupId) },
            mapper = { true }
        )
}
