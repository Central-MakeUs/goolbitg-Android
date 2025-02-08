package com.project.presentation.challenge.detail

sealed class ChallengeDetailEvent {
    data class InitChallengeId(val challengeId: Int) : ChallengeDetailEvent()
    data object CheckAttendance : ChallengeDetailEvent()
    data object StopChallenge: ChallengeDetailEvent()
}