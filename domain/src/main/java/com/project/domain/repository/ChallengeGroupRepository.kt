package com.project.domain.repository

import com.project.domain.model.DataState
import com.project.domain.model.challengegroup.ChallengeGroupListModel
import com.project.domain.model.challengegroup.ChallengeGroupModel
import com.project.domain.model.challengegroup.ChallengeGroupRankModel
import com.project.domain.model.challengegroup.ChallengeGroupRecordModel
import com.project.domain.model.challengegroup.ChallengeGroupTrippleModel
import kotlinx.coroutines.flow.Flow

interface ChallengeGroupRepository {
    suspend fun fetchChallengeGroupList(
        page: Int,
        size: Int,
        search: String?,
        created: Boolean?,
        participating: Boolean?
    ): Flow<DataState<ChallengeGroupListModel>>

    suspend fun createChallengeGroup(
        title: String,
        reward: Int,
        hashtags: List<String>,
        category: String,
        maxSize: Int,
        isHidden: Boolean,
        password: String?
    ): Flow<DataState<ChallengeGroupModel>>

    suspend fun fetchChallengeGroupDetail(groupId: Int): Flow<DataState<ChallengeGroupRankModel>>

    suspend fun updateChallengeGroup(
        groupId: Int,
        title: String,
        reward: Int,
        hashtags: List<String>,
        category: String,
        maxSize: Int,
        isHidden: Boolean,
        password: String?
    ): Flow<DataState<ChallengeGroupModel>>

    suspend fun deleteChallengeGroup(groupId: Int): Flow<DataState<Boolean>>

    suspend fun enrollChallengeGroup(groupId: Int, password: String?): Flow<DataState<Boolean>>

    suspend fun checkChallengeGroup(groupId: Int): Flow<DataState<ChallengeGroupRecordModel>>

    suspend fun fetchChallengeGroupTripple(groupId: Int): Flow<DataState<ChallengeGroupTrippleModel>>

    suspend fun exitChallengeGroup(groupId: Int): Flow<DataState<Boolean>>
}
