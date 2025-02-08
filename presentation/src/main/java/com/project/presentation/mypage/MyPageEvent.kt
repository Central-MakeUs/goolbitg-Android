package com.project.presentation.mypage

sealed class MyPageEvent {
    data object Logout: MyPageEvent()
}