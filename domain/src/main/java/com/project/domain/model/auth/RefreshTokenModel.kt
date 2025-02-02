package com.project.domain.model.auth

data class RefreshTokenModel(
    val accessToken: String,
    val refreshToken: String
)
