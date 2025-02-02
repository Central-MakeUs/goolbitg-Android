package com.project.data.remote.response.user

import com.google.gson.annotations.SerializedName
import com.project.domain.model.user.WeeklyRecordStatusModel
import com.project.domain.model.user.WeeklyStatusModel

data class GetWeeklyRecordStatusRes(
    @SerializedName("nickname") val nickname: String?,
    @SerializedName("saving") val saving: Int,
    @SerializedName("continueCount") val continueCount: Int?,
    @SerializedName("weeklyStatus") val weeklyStatus: List<WeeklyStatusRes>,
    @SerializedName("todayIndex") val todayIndex: Int
) {
    fun toDomainModel() = WeeklyRecordStatusModel(
        nickname = nickname,
        saving = saving,
        continueCount = continueCount,
        weeklyStatus = weeklyStatus.map { it.toDomainModel() },
        todayIndex = todayIndex,
    )
}

data class WeeklyStatusRes(
    @SerializedName("totalChallenges") val totalChallenges: Int,
    @SerializedName("achievedChallenges") val achievedChallenges: Int
) {
    fun toDomainModel() = WeeklyStatusModel(
        totalChallenges = totalChallenges,
        achievedChallenges = achievedChallenges,
    )
}
