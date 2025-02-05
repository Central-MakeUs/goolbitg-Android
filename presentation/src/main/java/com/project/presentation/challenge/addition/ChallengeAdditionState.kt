package com.project.presentation.challenge.addition

data class ChallengeAdditionState(
    val isBackEnabled: Boolean,
    val isLoading: Boolean
){
    companion object{
        fun create() = ChallengeAdditionState(
            isBackEnabled = true,
            isLoading = false
        )
    }
}