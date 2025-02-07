package com.project.presentation.challenge.addition

import com.project.domain.model.challenge.ChallengeInfoModel

data class ChallengeAdditionState(
    val nickname: String,
    val popularChallengeList: List<ChallengeInfoModel>,
    val etcChallengeList: List<ChallengeInfoModel>,
    val isBackEnabled: Boolean,
    val isEnrollSuccess: Boolean,
    val isLoading: Boolean,
    val isEnrollLoading: Boolean,
){
    companion object{
        fun create() = ChallengeAdditionState(
            nickname = "",
            popularChallengeList = listOf(),
            etcChallengeList = listOf(),
            isBackEnabled = true,
            isEnrollSuccess = false,
            isLoading = false,
            isEnrollLoading = false
        )
    }
}