package com.project.domain.usecase.buyornot

import com.project.domain.repository.BuyOrNotRepository
import javax.inject.Inject

class VotePostingUseCase @Inject constructor(
    private val buyOrNotRepository: BuyOrNotRepository
) {
    suspend operator fun invoke(postId: Int, vote: String) =
        buyOrNotRepository.votePosting(postId = postId, vote = vote)
}
