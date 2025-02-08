package com.project.presentation.challenge.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.data.preferences.AuthDataStore
import com.project.domain.model.DataState
import com.project.domain.usecase.challenge.CompleteTodayChallengeRecordUseCase
import com.project.domain.usecase.challenge.DeleteChallengeUseCase
import com.project.domain.usecase.challenge.FetchChallengeTripleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChallengeDetailViewModel @Inject constructor(
    private val authDataStore: AuthDataStore,
    private val fetchChallengeTripleUseCase: FetchChallengeTripleUseCase,
    private val completeTodayChallengeRecordUseCase: CompleteTodayChallengeRecordUseCase,
    private val deleteChallengeUseCase: DeleteChallengeUseCase
) : ViewModel() {
    private val _state: MutableStateFlow<ChallengeDetailState> =
        MutableStateFlow(ChallengeDetailState.create())
    val state get() = _state.asStateFlow()

    init {
        getLocalNickname()
    }

    fun onEvent(event: ChallengeDetailEvent) {
        when (event) {
            is ChallengeDetailEvent.InitChallengeId -> {
                _state.value = state.value.copy(
                    challengeId = event.challengeId
                )
                fetchChallengeTriple()
            }
            is ChallengeDetailEvent.CheckAttendance -> {
                checkAttendance()
            }
            is ChallengeDetailEvent.StopChallenge-> {
                stopChallenge()
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

    private fun fetchChallengeTriple(){
        viewModelScope.launch {
            state.value.challengeId?.let{
                fetchChallengeTripleUseCase(challengeId = it).collect{ result ->
                    when(result){
                        is DataState.Loading -> {
                            _state.value = state.value.copy(
                                isLoading = result.isLoading
                            )
                        }
                        is DataState.Success -> {
                            result.data?.let{
                                _state.value = state.value.copy(
                                    challengeTripleModel = it
                                )
                            }
                        }
                        else -> Unit
                    }
                }

            }
        }
    }

    private fun checkAttendance(){
        viewModelScope.launch {
            if(state.value.challengeId != null){
                completeTodayChallengeRecordUseCase(challengeId = state.value.challengeId!!).collect{ result ->
                    when(result){
                        is DataState.Loading -> {
                            _state.value = state.value.copy(
                                isLoading = result.isLoading
                            )
                        }
                        is DataState.Success -> {
                            fetchChallengeTriple()
                        }
                        else -> Unit
                    }
                }
            }
        }
    }

    private fun stopChallenge(){
        viewModelScope.launch {
            if(state.value.challengeId != null){
                deleteChallengeUseCase(challengeId = state.value.challengeId!!).collect{ result ->
                    when(result){
                        is DataState.Loading -> {
                            _state.value = state.value.copy(
                                isLoading = result.isLoading
                            )
                        }
                        is DataState.Success -> {
                            fetchChallengeTriple()
                        }
                        else -> Unit
                    }
                }

            }
        }
    }
}