package com.project.presentation.challengegroup.detail

sealed class ChallengeGroupDetailEvent {
    data class InitGroupId(val groupId: Int) : ChallengeGroupDetailEvent()
    object CheckToday : ChallengeGroupDetailEvent()
    object ExitGroup : ChallengeGroupDetailEvent()
    object DeleteGroup : ChallengeGroupDetailEvent()
}
