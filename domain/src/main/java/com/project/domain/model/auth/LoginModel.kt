package com.project.domain.model.auth

data class LoginModel(
    val accessToken: String,
    val refreshToken: String,
)
