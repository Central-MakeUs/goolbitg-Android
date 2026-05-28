package com.project.presentation.challengegroup.create

sealed class ChallengeGroupCreateEvent {
    data class ChangeTitle(val value: String) : ChallengeGroupCreateEvent()
    data class ChangeReward(val value: String) : ChallengeGroupCreateEvent()
    data class ChangeHashtagInput(val value: String) : ChallengeGroupCreateEvent()
    object AddHashtag : ChallengeGroupCreateEvent()
    data class RemoveHashtag(val tag: String) : ChallengeGroupCreateEvent()
    object OpenCategorySheet : ChallengeGroupCreateEvent()
    object CloseCategorySheet : ChallengeGroupCreateEvent()
    data class SelectCategory(val value: String) : ChallengeGroupCreateEvent()
    object IncreaseMaxSize : ChallengeGroupCreateEvent()
    object DecreaseMaxSize : ChallengeGroupCreateEvent()
    data class ToggleHidden(val value: Boolean) : ChallengeGroupCreateEvent()
    data class ChangePassword(val value: String) : ChallengeGroupCreateEvent()
    object Submit : ChallengeGroupCreateEvent()
}
