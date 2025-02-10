package com.project.presentation.challenge.main

import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.domain.model.DataState
import com.project.domain.model.user.WeeklyStatusModel
import com.project.domain.usecase.challenge.FetchEnrolledChallengeListUseCase
import com.project.domain.usecase.user.GetWeeklyRecordStatusUseCase
import com.project.presentation.base.extension.LocalDateExtension.toHalfFormat
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class ChallengeViewModel @Inject constructor(
    private val getWeeklyRecordStatusUseCase: GetWeeklyRecordStatusUseCase,
    private val fetchEnrolledChallengeListUseCase: FetchEnrolledChallengeListUseCase
) : ViewModel() {
    private val _state: MutableStateFlow<ChallengeState> = MutableStateFlow(ChallengeState.create())
    val state get() = _state.asStateFlow()

    private val _weeklyDataMap = mutableStateMapOf<Long, List<WeeklyStatusModel>>()
    val weeklyDataMap: Map<Long, List<WeeklyStatusModel>> = _weeklyDataMap


    init {
        fetchThreeWeekStatus(
            offset = (Int.MAX_VALUE / 2).toLong(),
            targetDate = state.value.selectedDate
        )
        fetchSelectedDateChallengeList()
    }

    fun onEvent(event: ChallengeEvent) {
        when (event) {
            is ChallengeEvent.ChangeSelectedDate -> {
                _state.value = state.value.copy(
                    selectedDate = event.date,
                    challengeList = listOf()
                )
                fetchSelectedDateChallengeList()
            }

            is ChallengeEvent.ChangePage -> {
                fetchThreeWeekStatus(offset = event.offset, targetDate = event.targetDate)
            }
        }
    }

    fun fetchThreeWeekStatus(offset: Long, targetDate: LocalDate? = null, isNew: Boolean = false) {
        viewModelScope.launch {
            fetchWeekStatus(
                offset = offset - 1,
                standardDate = targetDate?.minusWeeks(1),
                isNew = isNew
            )
            fetchWeekStatus(offset = offset, standardDate = targetDate, isNew = isNew)
            fetchWeekStatus(
                offset = offset + 1,
                standardDate = targetDate?.plusWeeks(1),
                isNew = isNew
            )
        }
    }

    private fun fetchWeekStatus(
        offset: Long,
        standardDate: LocalDate? = null,
        isNew: Boolean = false
    ) {
        viewModelScope.launch {
            if (isNew || !weeklyDataMap.containsKey(offset)) {
                getWeeklyRecordStatusUseCase(date = standardDate?.toHalfFormat()).collect { result ->
                    when (result) {
                        is DataState.Loading -> {
                            _state.value = state.value.copy(
                                isLoading = result.isLoading
                            )
                        }

                        is DataState.Success -> {
                            result.data?.let {
                                _weeklyDataMap[offset] = it.weeklyStatus
                            }

                        }

                        else -> Unit
                    }
                }
            }
        }
    }

    fun fetchSelectedDateChallengeList() {
        viewModelScope.launch {
            fetchEnrolledChallengeListUseCase(
                date = state.value.selectedDate.toHalfFormat()
            ).collect { result ->
                when (result) {
                    is DataState.Loading -> {
                        _state.value = state.value.copy(
                            isLoading = result.isLoading
                        )
                    }

                    is DataState.Success -> {
                        result.data?.let {
                            _state.value = state.value.copy(
                                challengeList = it.items
                            )
                        }
                    }

                    else -> Unit
                }
            }
        }
    }
}