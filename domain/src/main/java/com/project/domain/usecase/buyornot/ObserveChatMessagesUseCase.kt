package com.project.domain.usecase.buyornot

import com.project.domain.repository.BuyOrNotRepository
import javax.inject.Inject

class ObserveChatMessagesUseCase @Inject constructor(
    private val buyOrNotRepository: BuyOrNotRepository
) {
    suspend operator fun invoke(buyOrNotId: Int) =
        buyOrNotRepository.observeChatMessages(buyOrNotId = buyOrNotId)
}
