package com.project.presentation.home

import androidx.lifecycle.ViewModel
import com.project.presentation.item.HomeChallengeStamp
import com.project.presentation.item.HomeTodayChallenge
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(): ViewModel() {
    private val _state: MutableStateFlow<HomeState> = MutableStateFlow(HomeState.create())
    val state get() = _state.asStateFlow()

    init {
        val weekStampList = mutableListOf<HomeChallengeStamp>().apply {
            val curr = state.value.currDate
            val currDayOfWeek = state.value.currDate.dayOfWeek.value
            (1..<currDayOfWeek).forEach {
                add(
                    0, HomeChallengeStamp(
                        date = curr.minusDays(it.toLong()),
                        isToday = false,
                        isComplete = true
                    )
                )
            }
            add(
                HomeChallengeStamp(
                    date = curr,
                    isToday = true,
                    isComplete = true
                )
            )
            (currDayOfWeek + 1..7).forEach {
                add(
                    HomeChallengeStamp(
                        date = curr.plusDays((it - currDayOfWeek).toLong()),
                        isToday = false,
                        isComplete = false
                    )
                )
            }
        }
        val todayChallengeList = listOf(
            HomeTodayChallenge(id = 1, title = "야식 안시켜먹기", savedPrice = 15000, isChecked = false),
            HomeTodayChallenge(id = 2, title = "택시 안타고 대중교통 이용하기", savedPrice = 7000, isChecked = false),
            HomeTodayChallenge(id = 3, title = "아침마다 커피 안마시기", savedPrice = 2000, isChecked = false),
            HomeTodayChallenge(id = 4, title = "식비 만원 이하로 소비하기", savedPrice = 10000, isChecked = false),
            HomeTodayChallenge(id = 5, title = "배달어플 안쓰고 요리해서 먹기", savedPrice = 15000, isChecked = false),
        )

        _state.value = state.value.copy(
            challengeStampList = weekStampList,
            todayChallengeList = todayChallengeList
        )
    }
}
