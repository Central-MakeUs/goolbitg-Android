package com.project.presentation.challengegroup.search

import com.project.domain.model.challengegroup.ChallengeGroupModel

sealed class ChallengeGroupSearchEvent {
    data class ChangeQuery(val query: String) : ChallengeGroupSearchEvent()
    object Search : ChallengeGroupSearchEvent()
    data class SelectGroup(val group: ChallengeGroupModel) : ChallengeGroupSearchEvent()
    data class ChangePassword(val password: String) : ChallengeGroupSearchEvent()
    object ConfirmJoin : ChallengeGroupSearchEvent()
    object DismissDialog : ChallengeGroupSearchEvent()
}
