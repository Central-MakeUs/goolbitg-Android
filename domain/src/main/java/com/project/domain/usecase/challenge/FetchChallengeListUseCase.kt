package com.project.domain.usecase.challenge

import com.project.domain.repository.ChallengeRepository
import javax.inject.Inject

class FetchChallengeListUseCase @Inject constructor(
    private val challengeRepository: ChallengeRepository
) {
    suspend operator fun invoke(
        page: Int = 0,
        size: Int = 10,
        spendingTypeId: Int? = null
    ) = challengeRepository.fetchChallengeList(
        page = page,
        size = size,
        spendingTypeId = spendingTypeId
    )
}