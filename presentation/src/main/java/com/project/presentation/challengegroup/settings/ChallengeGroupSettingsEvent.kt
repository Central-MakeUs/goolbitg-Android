package com.project.presentation.challengegroup.settings

sealed class ChallengeGroupSettingsEvent {
    data class InitGroupId(val groupId: Int) : ChallengeGroupSettingsEvent()
    data class ToggleAlarm(val enabled: Boolean) : ChallengeGroupSettingsEvent()
    object DeleteGroup : ChallengeGroupSettingsEvent()
    object ExitGroup : ChallengeGroupSettingsEvent()
}
