package com.project.presentation.splash

import com.project.domain.model.user.RegisterStatus

data class SplashState(
    val registerStatus: RegisterStatus?
){
    companion object{
        fun create() = SplashState(
            registerStatus = null
        )
    }
}
