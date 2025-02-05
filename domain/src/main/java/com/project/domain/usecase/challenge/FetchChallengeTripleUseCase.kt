package com.project.domain.usecase.challenge

import com.project.domain.repository.ChallengeRepository
import javax.inject.Inject

class FetchChallengeTripleUseCase @Inject constructor(
    private val challengeRepository: ChallengeRepository
) {
    suspend operator fun invoke(challengeId: Int) =
        challengeRepository.fetchChallengeTriple(challengeId = challengeId)
}