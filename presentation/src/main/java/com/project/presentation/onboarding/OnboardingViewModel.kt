package com.project.presentation.onboarding

import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.data.preferences.AuthDataStore
import com.project.domain.model.DataState
import com.project.domain.usecase.user.CheckNicknameDuplicatedUseCase
import com.project.domain.usecase.user.GetUserInfoUseCase
import com.project.domain.usecase.user.SetUserAgreementUseCase
import com.project.domain.usecase.user.SetUserCheckListUseCase
import com.project.domain.usecase.user.SetUserHabitUseCase
import com.project.domain.usecase.user.SetUserInfoUseCase
import com.project.domain.usecase.user.SetUserPatternUseCase
import com.project.presentation.common.BirthStatus
import com.project.presentation.common.GenderEnum
import com.project.presentation.common.NicknameStatus
import com.project.presentation.util.Regex.nicknameKoreanEnglishOnlyRegex
import com.project.presentation.util.Regex.nicknameLengthRegex
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val authDataStore: AuthDataStore,
    private val checkNicknameDuplicatedUseCase: CheckNicknameDuplicatedUseCase,
    private val setUserAgreementUseCase: SetUserAgreementUseCase,
    private val setUserInfoUseCase: SetUserInfoUseCase,
    private val setUserCheckListUseCase: SetUserCheckListUseCase,
    private val setUserHabitUseCase: SetUserHabitUseCase,
    private val setUserPatternUseCase: SetUserPatternUseCase,
    private val getUserInfoUseCase: GetUserInfoUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(OnboardingState.create())
    val state get() = _state.asStateFlow()

    init {
        viewModelScope.launch {
            _state.value = state.value.copy(
                localNickname = authDataStore.getNickname().firstOrNull() ?: ""
            )
        }
    }

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
                        if(!this[event.idx].isChecked){
                            if(event.idx != 5){
                                this[5].isChecked = false
                            }else{
                                (0..4).forEach { this[it].isChecked = false }
                            }
                        }
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
                registerAgreementAndUserInfo(event.isAdvertisementAgreement)
            }

            is OnboardingEvent.RequestSetUserCheckList -> {
                setUserCheckList()
            }

            is OnboardingEvent.RequestSetUserHabit -> {
                setUserConsumeHabit()
            }

            is OnboardingEvent.RequestSetUserPattern -> {
                setUserConsumePattern()
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

    private fun registerAgreementAndUserInfo(isAdvertisementAgreement: Boolean){
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
                        ).collect{ result2 ->
                            when(result2){
                                is DataState.Loading -> {
                                    _state.value = _state.value.copy(
                                        isLoading = result2.isLoading
                                    )
                                }
                                is DataState.Success -> {
                                    authDataStore.setNickname(nickname = state.value.nickname)
                                    withContext(Dispatchers.Default){
                                        _state.value = _state.value.copy(
                                            isFirstOnboardingSuccess = true
                                        )
                                    }
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

    private fun setUserCheckList(){
        viewModelScope.launch {
            setUserCheckListUseCase(
                check1 = state.value.checkList[0].isChecked,
                check2 = state.value.checkList[1].isChecked,
                check3 = state.value.checkList[2].isChecked,
                check4 = state.value.checkList[3].isChecked,
                check5 = state.value.checkList[4].isChecked,
                check6 = state.value.checkList[5].isChecked,
            ).collect{ result ->
                when(result){
                    is DataState.Loading -> {
                        _state.value = state.value.copy(
                            isLoading = result.isLoading
                        )
                    }
                    is DataState.Success -> {
                        _state.value = state.value.copy(
                            isCheckListSuccess = true
                        )
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun setUserConsumeHabit(){
        viewModelScope.launch {
            setUserHabitUseCase(
                avgIncomePerMonth = state.value.monthAvgIncome.toInt(),
                avgSpendingPerMonth = state.value.monthAvgSaving.toInt()
            ).collect{ result ->
                when(result){
                    is DataState.Loading -> {
                        _state.value = state.value.copy(
                            isLoading = result.isLoading
                        )
                    }
                    is DataState.Success -> {
                        _state.value = state.value.copy(
                            isConsumeHabitSuccess = true
                        )
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun setUserConsumePattern(){
        viewModelScope.launch {
            if(state.value.majorExpenditureDayOfWeek != null){
                val hour = if(state.value.majorExpenditureAmpm == "PM") {
                    state.value.majorExpenditureHours.toInt() + 12
                }else{
                    state.value.majorExpenditureHours.toInt()
                }
                val time = LocalTime.of(hour, state.value.majorExpenditureMinutes.toInt(), 0)
                val timeFormatter = DateTimeFormatter.ofPattern("hh:mm:ss")
                val timeStr = time.format(timeFormatter)

                setUserPatternUseCase(
                    primeUseDay = state.value.majorExpenditureDayOfWeek!!.value,
                    primeUserTime = timeStr
                ).collect{ result ->
                    when(result){
                        is DataState.Loading -> {
                            _state.value = state.value.copy(
                                isLoading = result.isLoading
                            )
                        }
                        is DataState.Success -> {
                            _state.value = state.value.copy(
                                isConsumePatternSuccess = true
                            )
                        }
                        else -> Unit
                    }
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

     fun getUserInfo(){
        viewModelScope.launch {
            val startTime = System.currentTimeMillis()
            getUserInfoUseCase().collect{ result ->
                when(result){
                    is DataState.Success -> {
                        val elapsedTime = System.currentTimeMillis() - startTime
                        if(elapsedTime < 3000L){
                            delay(3000L - elapsedTime)
                        }
                        withContext(Dispatchers.Default){
                            result.data?.let{
                                _state.value = state.value.copy(
                                    isAnalysisSuccess = true,
                                    userInfoModel = it
                                )
                            }
                        }
                    }
                    else -> Unit
                 }
            }
        }
    }
}
