package com.project.presentation.onboarding

import android.util.Log
import com.project.presentation.common.BirthState
import com.project.presentation.common.GenderEnum
import com.project.presentation.common.NicknameState

data class OnboardingState(
    // flow1
    val nickname: String,
    val nicknameState: NicknameState,
    val year: String,
    val month: String,
    val day: String,
    val birthState: BirthState,
    val gender: GenderEnum?,

    // flow3
    val checkList: List<CheckListData>
) {
    /**
     * 온보딩 첫번째 과정에서 닉네임과 생년월일, 성별에 대해서
     * 제대로 된 입력이 모두 들어있을 때
     */
    fun isFirstOnboardingCompleted(): Boolean {
        return nicknameState == NicknameState.Completed
                && birthState == BirthState.Completed
                && gender != null
    }

    /**
     * 체크리스트에서 체크한 아이템이 하나라도 있을 때
     */
    fun isAnyChecklistItemChecked(): Boolean{
        checkList.forEach{
            if(it.isChecked) return true
        }
        return false
    }
    companion object {
        fun create() = OnboardingState(
            nickname = "",
            nicknameState = NicknameState.Completed,
            year = "",
            month = "",
            day = "",
            birthState = BirthState.Completed,
            gender = null,
            checkList = List(10) { CheckListData(question = "{체크리스트 문항}", isChecked = false) }
        )
    }
}

data class CheckListData(
    val question: String,
    var isChecked: Boolean
)