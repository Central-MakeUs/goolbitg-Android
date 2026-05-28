package com.project.presentation.challengegroup.detail

import com.project.domain.model.challengegroup.ChallengeGroupRankModel
import com.project.domain.model.challengegroup.ChallengeGroupTrippleModel

data class ChallengeGroupDetailState(
    val groupId: Int = 0,
    val isLoading: Boolean = false,
    val rankModel: ChallengeGroupRankModel? = null,
    val tripple: ChallengeGroupTrippleModel? = null,
    val isOwner: Boolean = false,
    val isChecked: Boolean = false,
    val errorMessage: String? = null
)
