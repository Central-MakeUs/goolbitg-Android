package com.project.presentation.onboarding

import android.util.Log
import androidx.lifecycle.ViewModel
import com.project.presentation.common.GenderEnum
import com.project.presentation.common.NicknameState
import com.project.presentation.util.Regex.nicknameKoreanEnglishOnlyRegex
import com.project.presentation.util.Regex.nicknameLengthRegex
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor() : ViewModel() {
    private val _state = MutableStateFlow(OnboardingState.create())
    val state get() = _state.asStateFlow()

    fun onEvent(event: OnboardingEvent) {
        when (event) {
            is OnboardingEvent.ChangeNickname -> {
                checkAndChangeNickname(newValue = event.newValue)
            }

            is OnboardingEvent.ChangeYear -> {
                _state.value = state.value.copy(
                    year = event.newValue
                )
            }

            is OnboardingEvent.ChangeMonth -> {
                _state.value = state.value.copy(
                    month = event.newValue
                )
            }

            is OnboardingEvent.ChangeDay -> {
                _state.value = state.value.copy(
                    day = event.newValue
                )
            }

            is OnboardingEvent.ClickMale -> {
                if(state.value.gender != GenderEnum.Male){
                    _state.value = state.value.copy(
                        gender = GenderEnum.Male
                    )
                }
            }

            is OnboardingEvent.ClickFemale -> {
                if(state.value.gender != GenderEnum.Female){
                    _state.value = state.value.copy(
                        gender = GenderEnum.Female
                    )
                }
            }

            is OnboardingEvent.ClickCheckListItem -> {
                _state.value = state.value.copy(
                    checkList = state.value.checkList.map { it.copy() }.apply{
                        this[event.idx].isChecked = !this[event.idx].isChecked
                    }
                )
            }
        }
    }

    private fun checkAndChangeNickname(newValue: String){
        // 공백 제거
        var value = newValue.trim()
        // 글자수 6자로 제한
        if(value.length > 6){
            value = value.substring(0..5)
        }
        val nicknameState = if(value.isEmpty()){
            NicknameState.Empty
        }else if(nicknameLengthRegex.matches(value)){
            NicknameState.SizeErr
        }else if(nicknameKoreanEnglishOnlyRegex.matches(value)){
            NicknameState.OnlyKrAndEnErr
        } else{
            NicknameState.NotErr
        }

        _state.value = state.value.copy(
            nickname = value,
            nicknameState = nicknameState
        )
    }
}