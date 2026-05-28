package com.project.presentation.challengegroup.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.domain.model.ChallengeStatus
import com.project.domain.model.DataState
import com.project.domain.usecase.challengegroup.CheckChallengeGroupUseCase
import com.project.domain.usecase.challengegroup.DeleteChallengeGroupUseCase
import com.project.domain.usecase.challengegroup.ExitChallengeGroupUseCase
import com.project.domain.usecase.challengegroup.FetchChallengeGroupDetailUseCase
import com.project.domain.usecase.challengegroup.FetchChallengeGroupTrippleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChallengeGroupDetailViewModel @Inject constructor(
    private val fetchDetailUseCase: FetchChallengeGroupDetailUseCase,
    private val fetchTrippleUseCase: FetchChallengeGroupTrippleUseCase,
    private val checkUseCase: CheckChallengeGroupUseCase,
    private val exitUseCase: ExitChallengeGroupUseCase,
    private val deleteUseCase: DeleteChallengeGroupUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ChallengeGroupDetailState())
    val state: StateFlow<ChallengeGroupDetailState> = _state.asStateFlow()

    private val _navigateBack = MutableSharedFlow<Unit>()
    val navigateBack = _navigateBack.asSharedFlow()

    fun onEvent(event: ChallengeGroupDetailEvent) {
        when (event) {
            is ChallengeGroupDetailEvent.InitGroupId -> {
                _state.update { it.copy(groupId = event.groupId) }
                loadData(event.groupId)
            }
            is ChallengeGroupDetailEvent.CheckToday -> checkToday()
            is ChallengeGroupDetailEvent.ExitGroup -> exitGroup()
            is ChallengeGroupDetailEvent.DeleteGroup -> deleteGroup()
        }
    }

    private fun loadData(groupId: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            launch {
                fetchDetailUseCase(groupId).collect { result ->
                    if (result is DataState.Success) {
                        _state.update { it.copy(rankModel = result.data, isLoading = false) }
                    }
                }
            }
            launch {
                fetchTrippleUseCase(groupId).collect { result ->
                    if (result is DataState.Success) {
                        _state.update { s ->
                            s.copy(
                                tripple = result.data,
                                isChecked = result.data?.check3 == ChallengeStatus.SUCCESS ||
                                        (result.data?.location == 1 && result.data?.check1 == ChallengeStatus.SUCCESS) ||
                                        (result.data?.location == 2 && result.data?.check2 == ChallengeStatus.SUCCESS)
                            )
                        }
                    }
                }
            }
        }
    }

    private fun checkToday() {
        val groupId = _state.value.groupId
        viewModelScope.launch {
            checkUseCase(groupId).collect { result ->
                if (result is DataState.Success) {
                    _state.update { it.copy(isChecked = true) }
                    loadData(groupId)
                }
            }
        }
    }

    private fun exitGroup() {
        viewModelScope.launch {
            exitUseCase(_state.value.groupId).collect { result ->
                if (result is DataState.Success) {
                    _navigateBack.emit(Unit)
                }
            }
        }
    }

    private fun deleteGroup() {
        viewModelScope.launch {
            deleteUseCase(_state.value.groupId).collect { result ->
                if (result is DataState.Success) {
                    _navigateBack.emit(Unit)
                }
            }
        }
    }
}
