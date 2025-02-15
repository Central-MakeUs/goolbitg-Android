package com.project.domain.usecase.buyornot

import com.project.domain.repository.BuyOrNotRepository
import javax.inject.Inject

class ModifyBuyOrNotPostingUseCase @Inject constructor(
    private val buyOrNotRepository: BuyOrNotRepository
) {
    suspend operator fun invoke(
        postId: Int,
        productName: String,
        productPrice: Int,
        productImageUrl: String,
        goodReason: String,
        badReason: String
    ) = buyOrNotRepository.modifyBuyOrNotPosting(
            postId = postId,
            productName = productName,
            productPrice = productPrice,
            productImageUrl = productImageUrl,
            goodReason = goodReason,
            badReason = badReason,
        )
}
