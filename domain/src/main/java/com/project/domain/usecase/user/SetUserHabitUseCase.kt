package com.project.domain.usecase.user

import com.project.domain.repository.UserRepository
import javax.inject.Inject

class SetUserHabitUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(
        avgIncomePerMonth: Int,
        avgSpendingPerMonth: Int
    ) = userRepository.setRemoteUserHabit(
        avgIncomePerMonth = avgIncomePerMonth,
        avgSpendingPerMonth = avgSpendingPerMonth,
    )
}
