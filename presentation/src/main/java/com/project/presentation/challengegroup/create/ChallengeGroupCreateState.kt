package com.project.presentation.challengegroup.create

data class ChallengeGroupCreateState(
    val title: String = "",
    val reward: String = "",
    val hashtagInput: String = "",
    val hashtags: List<String> = emptyList(),
    val category: String? = null,
    val maxSize: Int = MIN_PARTICIPANTS,
    val isHidden: Boolean = false,
    val password: String = "",
    val isCategorySheetOpen: Boolean = false,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,
) {
    val rewardInt: Int? get() = reward.replace(",", "").trim().toIntOrNull()

    /** 금액 입력 에러 (입력이 있을 때만 검사) */
    val rewardError: String?
        get() = when {
            reward.isBlank() -> null
            rewardInt == null -> "숫자만 입력해주세요"
            (rewardInt ?: 0) < MIN_REWARD -> "1,000원 이상 입력해주세요"
            (rewardInt ?: 0) > MAX_REWARD -> "1,000,000원 이하로 입력해주세요"
            else -> null
        }

    val canAddHashtag: Boolean get() = hashtagInput.isNotBlank() && hashtags.size < MAX_HASHTAGS
    val canIncreaseMaxSize: Boolean get() = maxSize < MAX_PARTICIPANTS
    val canDecreaseMaxSize: Boolean get() = maxSize > MIN_PARTICIPANTS

    /** 어떤 필드라도 사용자가 건드렸는지 — 뒤로가기 시 confirm 노출 여부 결정 */
    val isModified: Boolean
        get() = title.isNotBlank() ||
            reward.isNotBlank() ||
            hashtagInput.isNotBlank() ||
            hashtags.isNotEmpty() ||
            !category.isNullOrBlank() ||
            maxSize != MIN_PARTICIPANTS ||
            isHidden ||
            password.isNotBlank()

    /** 모든 필수 입력이 충족되었는지 — 하단 버튼 노출 조건 */
    val isValid: Boolean
        get() = title.isNotBlank() &&
            rewardInt != null && rewardError == null &&
            !category.isNullOrBlank() &&
            maxSize in MIN_PARTICIPANTS..MAX_PARTICIPANTS &&
            (!isHidden || password.isNotBlank())

    companion object {
        const val MIN_PARTICIPANTS = 2
        const val MAX_PARTICIPANTS = 10
        const val MAX_HASHTAGS = 3
        const val MIN_REWARD = 1_000
        const val MAX_REWARD = 1_000_000

        val CATEGORY_OPTIONS = listOf(
            "식비", "교통비", "쇼핑", "기타", "생활비"
        )
    }
}
