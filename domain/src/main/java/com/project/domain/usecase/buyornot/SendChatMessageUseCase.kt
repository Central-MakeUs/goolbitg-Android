package com.project.domain.usecase.buyornot

import com.project.domain.repository.BuyOrNotRepository
import javax.inject.Inject

class SendChatMessageUseCase @Inject constructor(
    private val buyOrNotRepository: BuyOrNotRepository
) {
    suspend operator fun invoke(
        buyOrNotId: Int,
        userId: String,
        username: String,
        content: String
    ) = buyOrNotRepository.sendChatMessage(
        buyOrNotId = buyOrNotId,
        userId = userId,
        username = username,
        content = content
    )
}
