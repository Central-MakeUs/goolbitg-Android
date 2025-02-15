package com.project.domain.usecase.buyornot

import com.project.domain.repository.BuyOrNotRepository
import javax.inject.Inject

class DeleteBuyOrNotPostingUseCase @Inject constructor(
    private val buyOrNoteRepository: BuyOrNotRepository
) {
    suspend operator fun invoke(postId: Int) =
        buyOrNoteRepository.deleteBuyOrNotPosting(postId = postId)
}
