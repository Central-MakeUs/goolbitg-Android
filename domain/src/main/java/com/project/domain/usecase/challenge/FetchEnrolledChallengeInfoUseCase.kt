package com.project.domain.usecase.challenge

import com.project.domain.repository.ChallengeRepository
import javax.inject.Inject

class FetchEnrolledChallengeInfoUseCase @Inject constructor(
    private val challengeRepository: ChallengeRepository
) {
    suspend operator fun invoke(
        challengeId: Int,
        date: String? = null
    ) = challengeRepository.fetchEnrolledChallengeInfo(
        challengeId = challengeId,
        date = date
    )
}