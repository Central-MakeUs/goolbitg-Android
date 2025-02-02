package com.project.domain.usecase.auth

import com.project.domain.repository.AuthRepository
import javax.inject.Inject

class WithdrawAccountUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke() = authRepository.withdrawAccount()
}
