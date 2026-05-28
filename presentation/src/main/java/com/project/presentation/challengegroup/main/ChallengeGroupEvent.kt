package com.project.presentation.challengegroup.main

sealed class ChallengeGroupEvent {
    object LoadGroupList : ChallengeGroupEvent()
    data class ToggleMyRoomOnly(val enabled: Boolean) : ChallengeGroupEvent()
    data class ChangeSearchQuery(val query: String) : ChallengeGroupEvent()
    object Search : ChallengeGroupEvent()
}
