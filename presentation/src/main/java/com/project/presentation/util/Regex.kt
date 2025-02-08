package com.project.presentation.util

object Regex{
    val nicknameKoreanEnglishOnlyRegex = "^[가-힣a-zA-Zㄱ-ㅎㅏ-ㅣ]+\$".toRegex()
    val nicknameLengthRegex = "^.{2,6}$".toRegex()
}