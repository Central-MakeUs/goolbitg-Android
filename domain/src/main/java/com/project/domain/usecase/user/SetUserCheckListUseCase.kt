package com.project.domain.usecase.user

import com.project.domain.repository.UserRepository
import javax.inject.Inject

class SetUserCheckListUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(
        check1: Boolean,
        check2: Boolean,
        check3: Boolean,
        check4: Boolean,
        check5: Boolean,
        check6: Boolean,
    ) = userRepository.setRemoteUserCheckList(
        check1 = check1,
        check2 = check2,
        check3 = check3,
        check4 = check4,
        check5 = check5,
        check6 = check6,
    )
}
