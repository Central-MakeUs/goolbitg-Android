package com.project.presentation.challenge.detail

import com.project.domain.model.challenge.ChallengeTripleModel

data class ChallengeDetailState(
    val challengeId: Int?,
    val nickname: String,
    val challengeTripleModel: ChallengeTripleModel?,
    val isLoading: Boolean
){
    companion object{
        fun create() = ChallengeDetailState(
            challengeId = null,
            nickname = "",
            challengeTripleModel = null,
            isLoading = false
        )
    }
}