package com.project.domain.usecase.auth

import com.project.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(type: String, idToken: String, fcmToken: String?) =
        authRepository.login(type = type, idToken = idToken, fcmToken = fcmToken)
}
