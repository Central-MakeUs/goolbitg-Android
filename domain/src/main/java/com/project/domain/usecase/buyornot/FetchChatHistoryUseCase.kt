package com.project.domain.usecase.buyornot

import com.project.domain.repository.BuyOrNotRepository
import javax.inject.Inject

class FetchChatHistoryUseCase @Inject constructor(
    private val buyOrNotRepository: BuyOrNotRepository
) {
    suspend operator fun invoke(postId: Int, chatLastId: Int? = null) =
        buyOrNotRepository.fetchChatHistory(postId = postId, chatLastId = chatLastId)
}
