package com.project.domain.usecase.buyornot

import com.project.domain.repository.BuyOrNotRepository
import javax.inject.Inject

class DisconnectChatUseCase @Inject constructor(
    private val buyOrNotRepository: BuyOrNotRepository
) {
    suspend operator fun invoke() = buyOrNotRepository.disconnectChat()
}
