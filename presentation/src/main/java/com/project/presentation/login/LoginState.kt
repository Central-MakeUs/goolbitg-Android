package com.project.presentation.login

data class LoginState(
    val isLoginSuccess: Boolean
){
    companion object{
        fun create() = LoginState(
            isLoginSuccess = false
        )
    }
}
