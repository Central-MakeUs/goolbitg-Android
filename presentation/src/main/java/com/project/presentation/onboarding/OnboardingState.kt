package com.project.presentation.onboarding

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.project.presentation.common.BirthStatus
import com.project.presentation.common.DayOfWeekEnum
import com.project.presentation.common.GenderEnum
import com.project.presentation.common.NicknameStatus
import com.project.presentation.item.CheckListEnum
import java.time.LocalDate

data class OnboardingState(
    val currDate: LocalDate,

    // flow1
    val nickname: String,
    val nicknameStatus: NicknameStatus,
    val year: String,
    val month: String,
    val day: String,
    val birthStatus: BirthStatus,
    val gender: GenderEnum?,
    val isFirstOnboardingSuccess: Boolean,
    val isCheckListSuccess: Boolean,
    val isConsumeHabitSuccess: Boolean,
    val isConsumePatternSuccess: Boolean,

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
    val sameTypeCount: Int,

    val isLoading: Boolean
) {
    /**
     * 온보딩 첫번째 과정
     * 닉네임과 생년월일, 성별에 대해서 제대로 된 입력이 모두 들어있을 때
     */
    fun isFirstOnboardingCompleted(): Boolean {
        return nicknameStatus == NicknameStatus.Completed
                && birthStatus == BirthStatus.Completed
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
            currDate = LocalDate.now(),
            nickname = "",
            nicknameStatus = NicknameStatus.Empty,
            year = "",
            month = "",
            day = "",
            birthStatus = BirthStatus.Empty,
            gender = null,
            isFirstOnboardingSuccess = false,
            isCheckListSuccess = false,
            isConsumeHabitSuccess = false,
            isConsumePatternSuccess = false,
            checkList = CheckListEnum.entries.map {
                CheckListData(
                    checkListStrId = it.strId,
                    isChecked = false
                )
            },
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
            isLoading = false
        )
    }
}

data class CheckListData(
    @StringRes val checkListStrId: Int,
    var isChecked: Boolean
)
