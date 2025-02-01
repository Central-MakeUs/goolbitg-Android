package com.project.presentation.home

import com.project.presentation.item.HomeChallengeStamp
import com.project.presentation.item.HomeTodayChallenge
import java.time.LocalDate

data class HomeState(
    val currDate: LocalDate,
    val totalPrice: Int?,
    val challengeStampList: List<HomeChallengeStamp>,
    val todayChallengeList: List<HomeTodayChallenge>,

    val isLoading: Boolean
){
    companion object{
        fun create() = HomeState(
            currDate = LocalDate.now(),
            totalPrice = null,
            challengeStampList = listOf(),
            todayChallengeList = listOf(),
            isLoading = false
        )
    }
}
