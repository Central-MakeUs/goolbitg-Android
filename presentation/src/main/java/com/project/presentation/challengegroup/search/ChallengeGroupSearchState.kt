package com.project.presentation.challengegroup.search

import com.project.domain.model.challengegroup.ChallengeGroupModel

data class ChallengeGroupSearchState(
    val query: String = "",
    val isLoading: Boolean = false,
    val results: List<ChallengeGroupModel> = emptyList(),
    val showJoinDialog: Boolean = false,
    val selectedGroup: ChallengeGroupModel? = null,
    val passwordInput: String = "",
    val errorMessage: String? = null
)
