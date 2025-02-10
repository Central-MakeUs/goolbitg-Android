package com.project.presentation.challenge.main

import com.project.domain.model.challenge.ChallengeRecordModel
import java.time.LocalDate

data class ChallengeState(
    val todayDate: LocalDate,
    val selectedDate: LocalDate,
    val challengeList: List<ChallengeRecordModel>,
    val isLoading: Boolean
){
    companion object{
        fun create(): ChallengeState{
            val currDate = LocalDate.now()
            return ChallengeState(
                todayDate = currDate,
                selectedDate = currDate,
                challengeList = listOf(),
                isLoading = true
            )
        }
    }
}