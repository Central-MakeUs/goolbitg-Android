package com.project.presentation.common

enum class NicknameState{
    Empty,          // 비어있음
    SizeErr,        // 글자수 만족하지 않음
    OnlyKrAndEnErr, // 입력 가능 문자 만족하지 않음
    NotErr,         // 만족하지 않는 조건 없음
    Duplicated,     // 중복검사 실패
    Completed,      // 모든 체크 및 검사 충족
}

enum class BirthState{
    Empty,
    NotFullFilled,
    Completed
}

enum class GenderEnum(val value: String) {
    Male("M"), Female("F")
}