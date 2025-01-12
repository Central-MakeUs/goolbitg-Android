package com.project.presentation.onboarding

import androidx.lifecycle.ViewModel
import com.project.presentation.common.GenderEnum
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
                _state.value = state.value.copy(
                    nickname = event.newValue
                )
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
        }
    }

}