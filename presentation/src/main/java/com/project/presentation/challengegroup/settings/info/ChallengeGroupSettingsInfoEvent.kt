package com.project.presentation.challengegroup.settings.info

sealed class ChallengeGroupSettingsInfoEvent {
    data class InitGroupId(val groupId: Int) : ChallengeGroupSettingsInfoEvent()
    data class ChangeTitle(val value: String) : ChallengeGroupSettingsInfoEvent()
    data class ChangeReward(val value: String) : ChallengeGroupSettingsInfoEvent()
    data class ChangeHashtags(val value: String) : ChallengeGroupSettingsInfoEvent()
    data class ChangeCategory(val value: String) : ChallengeGroupSettingsInfoEvent()
    data class ChangeMaxSize(val value: String) : ChallengeGroupSettingsInfoEvent()
    data class ToggleHidden(val value: Boolean) : ChallengeGroupSettingsInfoEvent()
    data class ChangePassword(val value: String) : ChallengeGroupSettingsInfoEvent()
    object SaveChanges : ChallengeGroupSettingsInfoEvent()
}
