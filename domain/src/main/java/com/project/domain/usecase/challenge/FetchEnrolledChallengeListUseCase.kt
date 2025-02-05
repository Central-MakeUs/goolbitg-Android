package com.project.domain.usecase.challenge

import com.project.domain.repository.ChallengeRepository
import javax.inject.Inject

class FetchEnrolledChallengeListUseCase @Inject constructor(
    private val challengeRepository: ChallengeRepository
) {
    suspend operator fun invoke(
        page: Int = 0,
        size: Int = 10,
        date: String? = null,
        status: String? = null
    ) = challengeRepository.fetchEnrolledChallengeList(
        page = page,
        size = size,
        date = date,
        status = status,
    )
}