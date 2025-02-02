package com.project.domain.usecase.user

import com.project.domain.repository.UserRepository
import javax.inject.Inject

class CheckRegisterStatusUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke() = userRepository.checkRegisterStatus()
}
