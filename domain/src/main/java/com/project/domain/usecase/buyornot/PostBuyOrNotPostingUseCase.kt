package com.project.domain.usecase.buyornot

import com.project.domain.repository.BuyOrNotRepository
import javax.inject.Inject

class PostBuyOrNotPostingUseCase @Inject constructor(
    private val buyOrNotRepository: BuyOrNotRepository
) {
    suspend operator fun invoke(
        productName: String,
        productPrice: Int,
        productImageUrl: String,
        goodReason: String,
        badReason: String,
    ) = buyOrNotRepository.postBuyOrNotPosting(
        productName = productName,
        productPrice = productPrice,
        productImageUrl = productImageUrl,
        goodReason = goodReason,
        badReason = badReason,
    )
}
