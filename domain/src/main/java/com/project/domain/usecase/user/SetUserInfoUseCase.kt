package com.project.domain.usecase.user

import com.project.domain.repository.UserRepository
import javax.inject.Inject

class SetUserInfoUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(
        nickname: String,
        birthday: String?,
        gender: String?
    ) = userRepository.setRemoteUserInfo(
        nickname = nickname,
        birthday = birthday,
        gender = gender,
    )
}
