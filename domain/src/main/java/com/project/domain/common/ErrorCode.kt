package com.project.domain.common

sealed class ErrorCode(val code: Int, val msg: String) {
    // Auth
    data object InvalidToken : ErrorCode(code = 2001, msg = "토큰이 유효하지 않습니다.")
    data object ExpiredToken : ErrorCode(code = 2002, msg = "토큰이 만료되었습니다.")
    data object AlreadyRegister : ErrorCode(code = 3001, msg = "이미 등록된 회원입니다.")
    data object NotRegisteredUser : ErrorCode(code = 3002, msg = "등록되지 않은 회원입니다.")
    data object PrevFlowNotCompleted : ErrorCode(code = 3004, msg = "이전 단계의 정보 입력이 완료되지 않았습니다.")
}
