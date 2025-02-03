package com.project.presentation.login

import com.project.domain.model.user.RegisterStatus

data class LoginState(
    val loginType: String,
    val registerStatus: RegisterStatus?,
    val isPermission: Boolean,
    val isLoading: Boolean
){
    companion object{
        fun create() = LoginState(
            loginType = "KAKAO",
            registerStatus = null,
            isPermission = false,
            isLoading = false
        )
    }
}
