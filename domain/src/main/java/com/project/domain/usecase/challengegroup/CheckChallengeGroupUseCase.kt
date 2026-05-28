package com.project.domain.usecase.challengegroup

import com.project.domain.repository.ChallengeGroupRepository
import javax.inject.Inject

class CheckChallengeGroupUseCase @Inject constructor(
    private val repository: ChallengeGroupRepository
) {
    suspend operator fun invoke(groupId: Int) =
        repository.checkChallengeGroup(groupId = groupId)
}
