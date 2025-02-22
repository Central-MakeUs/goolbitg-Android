package com.project.domain.usecase.buyornot

import com.project.domain.repository.BuyOrNotRepository
import javax.inject.Inject

class ReportPostingUseCase @Inject constructor(
    private val buyOrNotRepository: BuyOrNotRepository
) {
    suspend operator fun invoke(postId: Int, reason: String) =
        buyOrNotRepository.reportPosting(postId = postId, reason = reason)
}
