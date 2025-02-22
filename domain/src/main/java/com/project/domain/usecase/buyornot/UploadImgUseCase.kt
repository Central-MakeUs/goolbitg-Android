package com.project.domain.usecase.buyornot

import com.project.domain.repository.BuyOrNotRepository
import javax.inject.Inject

class UploadImgUseCase @Inject constructor(
    private val buyOrNotRepository: BuyOrNotRepository
) {
    suspend operator fun invoke(byteArray: ByteArray) =
        buyOrNotRepository.uploadImage(byteArray = byteArray)
}
