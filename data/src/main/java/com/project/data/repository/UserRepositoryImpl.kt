package com.project.data.repository

import com.project.data.remote.common.NetworkUtils
import com.project.data.remote.datasource.UserDataSource
import com.project.data.remote.request.user.CheckNicknameDuplicatedReq
import com.project.data.remote.request.user.UserAgreementReq
import com.project.data.remote.request.user.UserCheckListReq
import com.project.data.remote.request.user.UserHabitReq
import com.project.data.remote.request.user.UserInfoReq
import com.project.data.remote.request.user.UserPatternReq
import com.project.domain.model.DataState
import com.project.domain.model.user.CheckNicknameModel
import com.project.domain.model.user.RegisterStatusModel
import com.project.domain.model.user.UserInfoModel
import com.project.domain.model.user.WeeklyRecordStatusModel
import com.project.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userDataSource: UserDataSource
) : UserRepository {
    override suspend fun getRemoteUserInfo(): Flow<DataState<UserInfoModel>> {
        return NetworkUtils.handleApi(
            execute = { userDataSource.getRemoteUserInfo() },
            mapper = { it?.toDomainModel() }
        )
    }

    override suspend fun checkRegisterStatus(): Flow<DataState<RegisterStatusModel>> {
        return NetworkUtils.handleApi(
            execute = { userDataSource.checkRegisterStatus() },
            mapper = { it?.toDomainModel() }
        )
    }

    override suspend fun getWeeklyRecordStatus(date: String): Flow<DataState<WeeklyRecordStatusModel>> {
        return NetworkUtils.handleApi(
            execute = { userDataSource.getWeeklyRecordStatus(date = date) },
            mapper = { it?.toDomainModel() }
        )
    }

    override suspend fun checkNicknameDuplicated(nickname: String): Flow<DataState<CheckNicknameModel>> {
        return NetworkUtils.handleApi(
            execute = {
                userDataSource.checkNicknameDuplicated(
                    body = CheckNicknameDuplicatedReq(
                        nickname = nickname
                    )
                )
            },
            mapper = { it?.toDomainModel() }
        )
    }

    override suspend fun agreePushNotification(): Flow<DataState<Boolean>> {
        return NetworkUtils.handleApi(
            execute = {
                userDataSource.agreePushNotification()
            },
            mapper = { true }
        )
    }

    override suspend fun setRemoteUserAgreement(
        agreement1: Boolean,
        agreement2: Boolean,
        agreement3: Boolean,
        agreement4: Boolean
    ): Flow<DataState<Boolean>> {
        return NetworkUtils.handleApi(
            execute = {
                userDataSource.setRemoteUserAgreement(
                    body = UserAgreementReq(
                        agreement1 = agreement1,
                        agreement2 = agreement2,
                        agreement3 = agreement3,
                        agreement4 = agreement4,
                    )
                )
            },
            mapper = { true }
        )
    }

    override suspend fun setRemoteUserInfo(
        nickname: String,
        birthday: String,
        gender: String
    ): Flow<DataState<Boolean>> {
        return NetworkUtils.handleApi(
            execute = {
                userDataSource.setRemoteUserInfo(
                    body = UserInfoReq(
                        nickname = nickname,
                        birthday = birthday,
                        gender = gender,
                    )
                )
            },
            mapper = { true }
        )
    }

    override suspend fun setRemoteUserCheckList(
        check1: Boolean,
        check2: Boolean,
        check3: Boolean,
        check4: Boolean,
        check5: Boolean,
        check6: Boolean,
    ): Flow<DataState<Boolean>> {
        return NetworkUtils.handleApi(
            execute = {
                userDataSource.setRemoteUserCheckList(
                    body = UserCheckListReq(
                        check1 = check1,
                        check2 = check2,
                        check3 = check3,
                        check4 = check4,
                        check5 = check5,
                        check6 = check6,
                    )
                )
            },
            mapper = { true }
        )
    }

    override suspend fun setRemoteUserHabit(
        avgIncomePerMonth: Int,
        avgSpendingPerMonth: Int
    ): Flow<DataState<Boolean>> {
        return NetworkUtils.handleApi(
            execute = {
                userDataSource.setRemoteUserHabit(
                    body = UserHabitReq(
                        avgIncomePerMonth = avgIncomePerMonth,
                        avgSpendingPerMonth = avgSpendingPerMonth
                    )
                )
            },
            mapper = { true }
        )
    }

    override suspend fun setRemoteUserPattern(
        primeUseDay: String,
        primeUserTime: String
    ): Flow<DataState<Boolean>> {
        return NetworkUtils.handleApi(
            execute = {
                userDataSource.setRemoteUserPattern(
                    body = UserPatternReq(
                        primeUseDay = primeUseDay,
                        primeUserTime = primeUserTime
                    )
                )
            },
            mapper = { true }
        )
    }
}
