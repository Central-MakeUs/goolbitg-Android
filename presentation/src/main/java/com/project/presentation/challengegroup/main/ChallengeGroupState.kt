package com.project.presentation.challengegroup.main

import com.project.domain.model.challengegroup.ChallengeGroupModel

data class ChallengeGroupState(
    val isLoading: Boolean = false,
    /** 서버에서 받은 원본 그룹 목록 (참여 중인 그룹) */
    val allGroups: List<ChallengeGroupModel> = emptyList(),
    /** 현재 로그인 사용자 ID — "내가 만든 방만 보기" 필터링용 */
    val currentUserId: String? = null,
    val isMyRoomOnly: Boolean = false,
    val searchQuery: String = "",
    val errorMessage: String? = null
) {
    /**
     * UI 에 노출되는 목록.
     * "내가 만든 방만 보기" 토글 시 클라이언트에서 ownerId 로 필터링한다 (API 재호출 없음).
     */
    val groupList: List<ChallengeGroupModel>
        get() = if (isMyRoomOnly && !currentUserId.isNullOrBlank()) {
            allGroups.filter { it.ownerId == currentUserId }
        } else {
            allGroups
        }
}
