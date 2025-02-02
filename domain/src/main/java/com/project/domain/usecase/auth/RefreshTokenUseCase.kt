package com.project.domain.usecase.auth

import com.project.domain.repository.AuthRepository
import javax.inject.Inject

class RefreshTokenUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(refreshToken: String) =
        authRepository.refreshToken(refreshToken = refreshToken)
}
