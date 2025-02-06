package com.project.presentation.home

import com.project.domain.model.challenge.ChallengeRecordModel
import com.project.domain.model.user.WeeklyRecordStatusModel
import com.project.presentation.item.HomeChallengeStamp
import java.time.LocalDate

data class HomeState(
    val currDate: LocalDate,
    val totalPrice: Int?,
    val dayList: List<Int>,
    val weeklyRecordStatusModel: WeeklyRecordStatusModel?,
    val challengeRecordModel: List<ChallengeRecordModel>,

    val isLoading: Boolean
){
    companion object{
        fun create() = HomeState(
            currDate = LocalDate.now(),
            totalPrice = null,
            dayList = listOf(),
            weeklyRecordStatusModel = null,
            challengeRecordModel = listOf(),
            isLoading = false
        )
    }
}
