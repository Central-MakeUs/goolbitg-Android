package com.project.presentation.onboarding

import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.domain.model.DataState
import com.project.domain.usecase.user.CheckNicknameDuplicatedUseCase
import com.project.domain.usecase.user.SetUserAgreementUseCase
import com.project.domain.usecase.user.SetUserInfoUseCase
import com.project.presentation.common.BirthStatus
import com.project.presentation.common.GenderEnum
import com.project.presentation.common.NicknameStatus
import com.project.presentation.util.Regex.nicknameKoreanEnglishOnlyRegex
import com.project.presentation.util.Regex.nicknameLengthRegex
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val checkNicknameDuplicatedUseCase: CheckNicknameDuplicatedUseCase,
    private val setUserAgreementUseCase: SetUserAgreementUseCase,
    private val setUserInfoUseCase: SetUserInfoUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(OnboardingState.create())
    val state get() = _state.asStateFlow()

    fun onEvent(event: OnboardingEvent) {
        when (event) {
            is OnboardingEvent.ChangeNickname -> {
                checkAndChangeNickname(newValue = event.newValue)
            }

            is OnboardingEvent.ChangeYear -> {
                _state.value = state.value.copy(
                    year = event.newValue,
                )
                checkBirthStatus()
            }

            is OnboardingEvent.ChangeMonth -> {
                _state.value = state.value.copy(
                    month = event.newValue
                )
                checkBirthStatus()
            }

            is OnboardingEvent.ChangeDay -> {
                _state.value = state.value.copy(
                    day = event.newValue
                )
                checkBirthStatus()
            }

            is OnboardingEvent.ClickMale -> {
                if (state.value.gender != GenderEnum.Male) {
                    _state.value = state.value.copy(
                        gender = GenderEnum.Male
                    )
                }
            }

            is OnboardingEvent.ClickFemale -> {
                if (state.value.gender != GenderEnum.Female) {
                    _state.value = state.value.copy(
                        gender = GenderEnum.Female
                    )
                }
            }

            is OnboardingEvent.ClickCheckListItem -> {
                _state.value = state.value.copy(
                    checkList = state.value.checkList.map { it.copy() }.apply {
                        this[event.idx].isChecked = !this[event.idx].isChecked
                    }
                )
            }

            is OnboardingEvent.ChangeMonthAvgIncome -> {
                if (event.newValue.isDigitsOnly()) {
                    _state.value = _state.value.copy(
                        monthAvgIncome = event.newValue
                    )
                }
            }

            is OnboardingEvent.ChangeMonthAvgSaving -> {
                if (event.newValue.isDigitsOnly()) {
                    _state.value = _state.value.copy(
                        monthAvgSaving = event.newValue
                    )
                }
            }

            is OnboardingEvent.SelectMajorExpenditureDayOfWeek -> {
                _state.value = _state.value.copy(
                    majorExpenditureDayOfWeek = event.dayOfWeek
                )
            }

            is OnboardingEvent.ChangeMajorExpenditureHour -> {
                _state.value = _state.value.copy(
                    majorExpenditureHours = event.newValue
                )
            }

            is OnboardingEvent.ChangeMajorExpenditureMinute -> {
                _state.value = _state.value.copy(
                    majorExpenditureMinutes = event.newValue
                )
            }

            is OnboardingEvent.ChangeMajorExpenditureAmpm -> {
                _state.value = _state.value.copy(
                    majorExpenditureAmpm = event.newValue
                )
            }

            is OnboardingEvent.DuplicationCheck -> {
                checkDuplication()
            }

            is OnboardingEvent.RequestFirstOnboarding -> {
                registerAgreementInfo(event.isAdvertisementAgreement)
            }
        }
    }

    private fun checkDuplication() {
        viewModelScope.launch {
            checkNicknameDuplicatedUseCase(nickname = state.value.nickname).collect { result ->
                when (result) {
                    is DataState.Loading -> {
                        _state.value = _state.value.copy(
                            isLoading = result.isLoading
                        )
                    }
                    is DataState.Success -> {
                        if (result.data != null) {
                            val isDuplicated = result.data!!.duplicated
                            if (isDuplicated) {
                                _state.value = _state.value.copy(
                                    nicknameStatus = NicknameStatus.Duplicated
                                )
                            } else {
                                _state.value = _state.value.copy(
                                    nicknameStatus = NicknameStatus.Completed
                                )
                            }
                        }
                    }
                    else -> {}
                }
            }
        }
    }

    private fun registerAgreementInfo(isAdvertisementAgreement: Boolean){
        viewModelScope.launch {
            setUserAgreementUseCase(
                agreement1 = true,
                agreement2 = true,
                agreement3 = true,
                agreement4 = isAdvertisementAgreement
            ).collect{ result ->
                when(result){
                    is DataState.Loading -> {
                        _state.value = _state.value.copy(
                            isLoading = result.isLoading
                        )
                    }
                    is DataState.Success -> {
                        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                        val date = LocalDate.of(state.value.year.toInt(), state.value.month.toInt(), state.value.day.toInt())
                        setUserInfoUseCase(
                            nickname = state.value.nickname,
                            birthday = date.format(formatter),
                            gender = state.value.gender?.value ?: GenderEnum.Male.value
                        ).collect{ result ->
                            when(result){
                                is DataState.Loading -> {
                                    _state.value = _state.value.copy(
                                        isLoading = result.isLoading
                                    )
                                }
                                is DataState.Success -> {
                                    _state.value = _state.value.copy(
                                        isFirstOnboardingSuccess = true
                                    )
                                }
                                else -> {}
                            }
                        }
                    }
                    else -> {}
                }
            }
        }
    }

    private fun checkAndChangeNickname(newValue: String) {
        // 글자수 6자로 제한
        if (newValue.length > 6 || newValue.contains(" ")) return

        val nicknameStatus = if (newValue.isEmpty()) {
            NicknameStatus.Empty
        } else if (!nicknameLengthRegex.matches(newValue)) {
            NicknameStatus.SizeErr
        } else if (!nicknameKoreanEnglishOnlyRegex.matches(newValue)) {
            NicknameStatus.OnlyKrAndEnErr
        } else {
            NicknameStatus.NotErr
        }

        _state.value = state.value.copy(
            nickname = newValue,
            nicknameStatus = nicknameStatus
        )
    }

    private fun checkBirthStatus() {
        viewModelScope.launch {
            val birthStatus =
                if (state.value.year.isNotEmpty()
                    && state.value.month.isNotEmpty()
                    && state.value.day.isNotEmpty()
                ) {
                    BirthStatus.Completed
                } else {
                    BirthStatus.Empty
                }
            _state.value = _state.value.copy(
                birthStatus = birthStatus
            )
        }
    }
}
