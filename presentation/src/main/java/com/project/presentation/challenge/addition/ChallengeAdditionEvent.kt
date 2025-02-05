package com.project.presentation.challenge.addition

sealed class ChallengeAdditionEvent {
    data class BackPressOption(val isBackEnabled: Boolean): ChallengeAdditionEvent()
}