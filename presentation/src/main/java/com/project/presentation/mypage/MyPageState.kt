package com.project.presentation.mypage

import com.project.domain.model.user.UserInfoModel

data class MyPageState(
    val userInfoModel: UserInfoModel?,
    val isLogoutSuccess: Boolean,
    val isLoading: Boolean
){
    companion object{
        fun create() = MyPageState(
            userInfoModel = null,
            isLogoutSuccess = false,
            isLoading = false
        )
    }
}