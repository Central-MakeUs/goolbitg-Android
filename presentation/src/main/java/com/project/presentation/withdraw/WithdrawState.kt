package com.project.presentation.withdraw

data class WithdrawState(
    val isWithdrawSuccess: Boolean,
    val isLoading: Boolean
){
    companion object{
        fun create() = WithdrawState(
            isWithdrawSuccess = false,
            isLoading = false
        )
    }
}