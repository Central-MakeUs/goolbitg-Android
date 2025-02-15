package com.project.domain.usecase.buyornot

import com.project.domain.repository.BuyOrNotRepository
import javax.inject.Inject

class FetchBuyOrNotPostsUseCase @Inject constructor(
    private val buyOrNotRepository: BuyOrNotRepository
) {
    suspend operator fun invoke(
        page: Int,
        size: Int,
        isCreated: Boolean
    ) = buyOrNotRepository.fetchBuyOrNotPosts(
        page = page,
        size = size,
        isCreated = isCreated,
    )
}
