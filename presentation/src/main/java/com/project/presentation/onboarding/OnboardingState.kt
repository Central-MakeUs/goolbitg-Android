package com.project.presentation.onboarding

import android.icu.util.GregorianCalendar
import com.project.presentation.common.BirthState
import com.project.presentation.common.DayOfWeekEnum
import com.project.presentation.common.GenderEnum
import com.project.presentation.common.NicknameState

data class OnboardingState(
    val currCalendar: GregorianCalendar,

    // flow1
    val nickname: String,
    val nicknameState: NicknameState,
    val year: String,
    val month: String,
    val day: String,
    val birthState: BirthState,
    val gender: GenderEnum?,

    // flow3
    val checkList: List<CheckListData>,

    // flow4
    val monthAvgIncome: String,
    val monthAvgSaving: String,

    // flow5
    val majorExpenditureDayOfWeek: DayOfWeekEnum?,
    val majorExpenditureHours: String,
    val majorExpenditureMinutes: String,
    val majorExpenditureAmpm: String,

    // Consume Type Result
    val consumeType: String,
    val consumeTypeSub: String,
    val myConsumeScore: Int,
    val sameTypeCount: Int
) {
    /**
     * 온보딩 첫번째 과정
     * 닉네임과 생년월일, 성별에 대해서 제대로 된 입력이 모두 들어있을 때
     */
    fun isFirstOnboardingCompleted(): Boolean {
        return nicknameState == NicknameState.Completed
                && birthState == BirthState.Completed
                && gender != null
    }

    /**
     * 온보딩 세번째 과정
     * 체크리스트에서 체크한 아이템이 하나라도 있을 때
     */
    fun isThirdOnboardingCompleted(): Boolean {
        checkList.forEach {
            if (it.isChecked) return true
        }
        return false
    }

    /**
     * 온보딩 네번째 과정에서 모든 월 평균 값들을 입력한 경우
     */
    fun isFourthOnboardingCompleted(): Boolean {
        return monthAvgIncome.isNotEmpty() && monthAvgSaving.isNotEmpty()
    }

    companion object {
        fun create() = OnboardingState(
            currCalendar = GregorianCalendar(),
            nickname = "",
            nicknameState = NicknameState.Completed,
            year = "",
            month = "",
            day = "",
            birthState = BirthState.Completed,
            gender = null,
            checkList = List(10) { CheckListData(question = "{체크리스트 문항}", isChecked = false) },
            monthAvgIncome = "",
            monthAvgSaving = "",
            majorExpenditureDayOfWeek = null,
            majorExpenditureHours = "",
            majorExpenditureAmpm = "",
            majorExpenditureMinutes = "",
            consumeType = "",
            consumeTypeSub = "",
            myConsumeScore = 0,
            sameTypeCount = 0,
        )
    }
}

data class CheckListData(
    val question: String,
    var isChecked: Boolean
)