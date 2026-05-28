package com.project.domain.usecase.challengegroup

import com.project.domain.repository.ChallengeGroupRepository
import javax.inject.Inject

class UpdateChallengeGroupUseCase @Inject constructor(
    private val repository: ChallengeGroupRepository
) {
    suspend operator fun invoke(
        groupId: Int,
        title: String,
        reward: Int,
        hashtags: List<String>,
        category: String,
        maxSize: Int,
        isHidden: Boolean,
        password: String?
    ) = repository.updateChallengeGroup(
        groupId = groupId,
        title = title,
        reward = reward,
        hashtags = hashtags,
        category = category,
        maxSize = maxSize,
        isHidden = isHidden,
        password = password
    )
}
