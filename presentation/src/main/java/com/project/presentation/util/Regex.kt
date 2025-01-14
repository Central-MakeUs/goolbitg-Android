package com.project.presentation.util

object Regex{
    val nicknameKoreanEnglishOnlyRegex = "^[가-힣a-zA-Z]+\$".toRegex()
    val nicknameLengthRegex = "^.{2,6}$".toRegex()
}