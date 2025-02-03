package com.project.presentation.common

import androidx.annotation.StringRes
import com.project.presentation.R

enum class NicknameStatus(@StringRes val strId: Int? = null){
    Empty,          // 비어있음
    SizeErr(strId = R.string.onboarding_first_nickname_err_1),        // 글자수 만족하지 않음
    OnlyKrAndEnErr(strId = R.string.onboarding_first_nickname_err_2), // 입력 가능 문자 만족하지 않음
    NotErr,         // 만족하지 않는 조건 없음
    Duplicated(strId = R.string.onboarding_first_nickname_err_3),     // 중복검사 실패
    Completed(strId = R.string.onboarding_first_nickname_completed),      // 모든 체크 및 검사 충족
}

enum class BirthStatus{
    Empty,
    Completed
}

enum class GenderEnum(val value: String) {
    Male("MALE"), Female("FEMALE")
}
