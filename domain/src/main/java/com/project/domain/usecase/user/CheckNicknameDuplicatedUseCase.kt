package com.project.domain.usecase.user

import com.project.domain.repository.UserRepository
import javax.inject.Inject

class CheckNicknameDuplicatedUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(nickname: String) =
        userRepository.checkNicknameDuplicated(nickname = nickname)
}
