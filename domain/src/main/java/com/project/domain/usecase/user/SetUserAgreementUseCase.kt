package com.project.domain.usecase.user

import com.project.domain.repository.UserRepository
import javax.inject.Inject

class SetUserAgreementUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(
        agreement1: Boolean,
        agreement2: Boolean,
        agreement3: Boolean,
        agreement4: Boolean
    ) = userRepository.setRemoteUserAgreement(
        agreement1 = agreement1,
        agreement2 = agreement2,
        agreement3 = agreement3,
        agreement4 = agreement4,
    )
}
