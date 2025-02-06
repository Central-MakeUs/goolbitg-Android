package com.project.domain.repository

import com.project.domain.model.DataState
import com.project.domain.model.user.CheckNicknameModel
import com.project.domain.model.user.RegisterStatusModel
import com.project.domain.model.user.UserInfoModel
import com.project.domain.model.user.WeeklyRecordStatusModel
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun getRemoteUserInfo(): Flow<DataState<UserInfoModel>>
    suspend fun checkRegisterStatus(): Flow<DataState<RegisterStatusModel>>
    suspend fun getWeeklyRecordStatus(date: String?): Flow<DataState<WeeklyRecordStatusModel>>
    suspend fun checkNicknameDuplicated(nickname: String): Flow<DataState<CheckNicknameModel>>
    suspend fun agreePushNotification(): Flow<DataState<Boolean>>
    suspend fun setRemoteUserAgreement(
        agreement1: Boolean,
        agreement2: Boolean,
        agreement3: Boolean,
        agreement4: Boolean
    ): Flow<DataState<Boolean>>
    suspend fun setRemoteUserInfo(
        nickname: String,
        birthday: String,
        gender: String
    ): Flow<DataState<Boolean>>
    suspend fun setRemoteUserCheckList(
        check1: Boolean,
        check2: Boolean,
        check3: Boolean,
        check4: Boolean,
        check5: Boolean,
        check6: Boolean,
    ): Flow<DataState<Boolean>>
    suspend fun setRemoteUserHabit(
        avgIncomePerMonth: Int,
        avgSpendingPerMonth: Int
    ): Flow<DataState<Boolean>>
    suspend fun setRemoteUserPattern(
        primeUseDay: String,
        primeUserTime: String
    ): Flow<DataState<Boolean>>
}
