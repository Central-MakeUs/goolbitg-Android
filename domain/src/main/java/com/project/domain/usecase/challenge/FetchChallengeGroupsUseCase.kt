package com.project.domain.usecase.challenge

import com.project.domain.repository.ChallengeRepository
import javax.inject.Inject

class FetchChallengeGroupsUseCase @Inject constructor(
    private val challengeRepository: ChallengeRepository
) {
    suspend operator fun invoke(
        search: String? = null,
        created: Boolean = false
    ) = challengeRepository.fetchChallengeGroups(
        search = search,
        created = created
    )
}
