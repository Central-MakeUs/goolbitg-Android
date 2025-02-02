package com.project.domain.usecase.auth

import com.project.domain.repository.AuthRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(type: String, idToken: String) =
        authRepository.register(
            type = type,
            idToken = idToken
        )
}
