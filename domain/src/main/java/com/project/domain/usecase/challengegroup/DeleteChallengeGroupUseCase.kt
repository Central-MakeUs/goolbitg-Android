package com.project.domain.usecase.challengegroup

import com.project.domain.repository.ChallengeGroupRepository
import javax.inject.Inject

class DeleteChallengeGroupUseCase @Inject constructor(
    private val repository: ChallengeGroupRepository
) {
    suspend operator fun invoke(groupId: Int) =
        repository.deleteChallengeGroup(groupId = groupId)
}
