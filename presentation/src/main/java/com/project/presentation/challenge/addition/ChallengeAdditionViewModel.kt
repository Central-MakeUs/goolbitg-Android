package com.project.presentation.challenge.addition

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.data.preferences.AuthDataStore
import com.project.domain.model.DataState
import com.project.domain.usecase.challenge.EnrollChallengeUseCase
import com.project.domain.usecase.challenge.FetchChallengeListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChallengeAdditionViewModel @Inject constructor(
    private val authDataStore: AuthDataStore,
    private val fetchChallengeListUseCase: FetchChallengeListUseCase,
    private val enrollChallengeUseCase: EnrollChallengeUseCase
) : ViewModel() {
    private val _state: MutableStateFlow<ChallengeAdditionState> =
        MutableStateFlow(ChallengeAdditionState.create())
    val state get() = _state.asStateFlow()

    init {
        getLocalNickname()
        fetchChallengeList()
    }

    fun onEvent(event: ChallengeAdditionEvent){
        when(event){
            is ChallengeAdditionEvent.BackPressOption -> {
                _state.value = state.value.copy(
                    isBackEnabled = event.isBackEnabled
                )
            }
            is ChallengeAdditionEvent.EnrollChallenge -> {
                enrollChallenge(challengeId = event.challengeId)
            }
        }
    }

    private fun getLocalNickname(){
        viewModelScope.launch {
            authDataStore.getNickname().collect{ nickname ->
                _state.value = state.value.copy(
                    nickname = nickname ?: ""
                )
            }
        }
    }

    private fun fetchChallengeList(){
        viewModelScope.launch {
            fetchChallengeListUseCase().collect{ result ->
                when(result){
                    is DataState.Loading -> {
                        _state.value = state.value.copy(
                            isLoading = result.isLoading
                        )
                    }
                    is DataState.Success -> {
                        result.data?.let {
                            val popularChallengeCount = 3
                            _state.value = state.value.copy(
                                popularChallengeList = it.items.subList(0, popularChallengeCount),
                                etcChallengeList = it.items.subList(popularChallengeCount, it.items.size)
                            )
                        }
                    }
                    else -> Unit
                }

            }
        }
    }

    private fun enrollChallenge(challengeId: Int){
        viewModelScope.launch {
            enrollChallengeUseCase(challengeId = challengeId).collect{ result ->
                when(result){
                    is DataState.Loading -> {
                        _state.value = state.value.copy(
                            isEnrollLoading = result.isLoading
                        )
                    }
                    is DataState.Success -> {
                        _state.value = state.value.copy(
                            isEnrollSuccess = true
                        )
                    }
                    else -> Unit
                }
            }
        }
    }

}