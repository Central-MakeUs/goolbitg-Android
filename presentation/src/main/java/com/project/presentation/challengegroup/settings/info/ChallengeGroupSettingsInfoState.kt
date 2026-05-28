package com.project.presentation.challengegroup.settings.info

import com.project.domain.model.challengegroup.ChallengeGroupModel

data class ChallengeGroupSettingsInfoState(
    val groupId: Int = 0,
    val group: ChallengeGroupModel? = null,
    val title: String = "",
    val reward: String = "",
    val hashtags: String = "",
    val category: String = "",
    val maxSize: String = "10",
    val isHidden: Boolean = false,
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) {
    val isValid: Boolean get() = title.isNotBlank() && reward.isNotBlank() && category.isNotBlank()
}
