package com.project.domain.model.user

enum class RegisterStatus(val code: Int) {
    FirstLogin(code = 0),
    TermsOfServices(code = 1),
    UserInfo(code = 2),
    CheckList(code = 3),
    ConsumeHabit(code = 4),
    ConsumePattern(code = 5),
    AddChallenge(code = 6)
}
