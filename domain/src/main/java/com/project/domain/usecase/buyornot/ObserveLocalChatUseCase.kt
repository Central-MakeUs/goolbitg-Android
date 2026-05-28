package com.project.domain.usecase.buyornot

import com.project.domain.repository.BuyOrNotRepository
import javax.inject.Inject

class ObserveLocalChatUseCase @Inject constructor(
    private val buyOrNotRepository: BuyOrNotRepository
) {
    operator fun invoke(postId: Int) = buyOrNotRepository.observeLocalChat(postId = postId)
}
