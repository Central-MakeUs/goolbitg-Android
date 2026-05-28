package com.project.presentation.challengegroup.settings

import com.project.domain.model.challengegroup.ChallengeGroupModel

data class ChallengeGroupSettingsState(
    val groupId: Int = 0,
    val group: ChallengeGroupModel? = null,
    val isOwner: Boolean = false,
    val isAlarmOn: Boolean = true,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) {
    /** 생성자 본인을 제외한 다른 참여자가 1명 이상인지 */
    val hasOtherParticipants: Boolean
        get() = (group?.peopleCount ?: 0) > 1

    /** 삭제 가능 여부 — 생성자이고 본인 외 참여자가 없을 때만 가능 */
    val canDelete: Boolean
        get() = isOwner && !hasOtherParticipants
}
