package com.project.presentation.buyornot.add

data class BuyOrNotAddState(
    val tabIdx: Int? = null,
    val postId: Int? = null,
    val prevImgUrl: String? = null,
    val currCroppedImgUri: String? = null,
    val resultImg: String? = null,
    val productName: String = "",
    val price: String = "",
    val goodReason: String = "",
    val badReason: String = "",
    val isPostingSuccess: Boolean = false,

    val isLoading: Boolean = false
) {
    fun checkState(): Boolean {
        val imgState = !prevImgUrl.isNullOrEmpty() || !currCroppedImgUri.isNullOrEmpty()
        return imgState && productName.isNotEmpty() && price.isNotEmpty() && goodReason.isNotEmpty() && badReason.isNotEmpty()
    }
}
