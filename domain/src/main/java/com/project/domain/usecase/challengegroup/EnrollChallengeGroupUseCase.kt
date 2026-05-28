package com.project.domain.usecase.challengegroup

import com.project.domain.repository.ChallengeGroupRepository
import javax.inject.Inject

class EnrollChallengeGroupUseCase @Inject constructor(
    private val repository: ChallengeGroupRepository
) {
    suspend operator fun invoke(groupId: Int, password: String? = null) =
        repository.enrollChallengeGroup(groupId = groupId, password = password)
}
