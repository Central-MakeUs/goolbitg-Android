package com.project.domain.usecase.user

import com.project.domain.repository.UserRepository
import javax.inject.Inject

class SetUserPatternUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(
        primeUseDay: String,
        primeUserTime: String
    ) = userRepository.setRemoteUserPattern(
        primeUseDay = primeUseDay,
        primeUserTime = primeUserTime,
    )
}
