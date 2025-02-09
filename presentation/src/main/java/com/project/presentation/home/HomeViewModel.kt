package com.project.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.domain.model.DataState
import com.project.domain.model.user.WeeklyRecordStatusModel
import com.project.domain.usecase.challenge.FetchEnrolledChallengeListUseCase
import com.project.domain.usecase.user.GetWeeklyRecordStatusUseCase
import com.project.presentation.item.HomeChallengeStamp
import com.project.presentation.item.HomeTodayChallenge
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val fetchEnrolledChallengeListUseCase: FetchEnrolledChallengeListUseCase,
    private val getWeeklyRecordStatusUseCase: GetWeeklyRecordStatusUseCase
): ViewModel() {
    private val _state: MutableStateFlow<HomeState> = MutableStateFlow(HomeState.create())
    val state get() = _state.asStateFlow()

    init {
        getWeekDays()
        getWeeklyRecordStatus()
        fetchEnrolledChallengeList()
    }

    fun getWeekDays(){
        val curr = state.value.currDate
        val currDayOfWeek = state.value.currDate.dayOfWeek.value
        val dayList = mutableListOf<Int>().apply{
            (1..<currDayOfWeek).forEach {
                add(0, curr.minusDays(it.toLong()).dayOfMonth)
            }
            add(curr.dayOfMonth)
            (currDayOfWeek + 1..7).forEach {
                add(curr.plusDays((it - currDayOfWeek).toLong()).dayOfMonth)
            }
        }
        _state.value = _state.value.copy(
            dayList = dayList
        )
    }

    fun getWeeklyRecordStatus(){
        viewModelScope.launch {
            getWeeklyRecordStatusUseCase().collect{ result ->
                when(result){
                    is DataState.Loading -> {
                        _state.value = state.value.copy(
                            isLoading = result.isLoading
                        )
                    }
                    is DataState.Success -> {
                        result.data?.let {
                            _state.value = state.value.copy(
                                weeklyRecordStatusModel = result.data
                            )
                        }
                    }
                    else -> Unit
                }
            }
        }
    }

    fun fetchEnrolledChallengeList(){
        viewModelScope.launch {
            fetchEnrolledChallengeListUseCase().collect{ result ->
                when(result){
                    is DataState.Loading -> {
                        _state.value = _state.value.copy(
                            isLoading = result.isLoading
                        )
                    }
                    is DataState.Success -> {
                        result.data?.let{
                            _state.value = _state.value.copy(
                                challengeRecordModel = result.data!!.items
                            )
                        }
                    }
                    else -> Unit
                }
            }
        }
    }
}
