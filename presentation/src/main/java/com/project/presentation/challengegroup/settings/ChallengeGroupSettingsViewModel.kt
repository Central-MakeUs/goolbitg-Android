package com.project.presentation.challengegroup.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.domain.model.DataState
import com.project.domain.usecase.challengegroup.DeleteChallengeGroupUseCase
import com.project.domain.usecase.challengegroup.ExitChallengeGroupUseCase
import com.project.domain.usecase.challengegroup.FetchChallengeGroupDetailUseCase
import com.project.domain.usecase.user.GetUserInfoUseCase
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
class ChallengeGroupSettingsViewModel @Inject constructor(
    private val fetchDetailUseCase: FetchChallengeGroupDetailUseCase,
    private val deleteUseCase: DeleteChallengeGroupUseCase,
    private val exitUseCase: ExitChallengeGroupUseCase,
    private val getUserInfoUseCase: GetUserInfoUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ChallengeGroupSettingsState())
    val state: StateFlow<ChallengeGroupSettingsState> = _state.asStateFlow()

    private val _navigateBack = MutableSharedFlow<Unit>()
    val navigateBack = _navigateBack.asSharedFlow()

    private var currentUserId: String? = null

    fun onEvent(event: ChallengeGroupSettingsEvent) {
        when (event) {
            is ChallengeGroupSettingsEvent.InitGroupId -> {
                _state.update { it.copy(groupId = event.groupId) }
                load(event.groupId)
            }
            is ChallengeGroupSettingsEvent.ToggleAlarm ->
                _state.update { it.copy(isAlarmOn = event.enabled) }
            is ChallengeGroupSettingsEvent.DeleteGroup -> deleteGroup()
            is ChallengeGroupSettingsEvent.ExitGroup -> exitGroup()
        }
    }

    private fun load(groupId: Int) {
        viewModelScope.launch {
            // 사용자 정보 + 그룹 상세를 받아 isOwner 계산
            launch {
                getUserInfoUseCase().collect { result ->
                    if (result is DataState.Success) {
                        currentUserId = result.data?.id
                        recomputeOwner()
                    }
                }
            }
            launch {
                fetchDetailUseCase(groupId).collect { result ->
                    if (result is DataState.Success) {
                        val group = result.data?.group ?: return@collect
                        _state.update { it.copy(group = group) }
                        recomputeOwner()
                    }
                }
            }
        }
    }

    private fun recomputeOwner() {
        val ownerId = _state.value.group?.ownerId ?: return
        val uid = currentUserId ?: return
        _state.update { it.copy(isOwner = uid == ownerId) }
    }

    private fun deleteGroup() {
        if (!_state.value.canDelete) return
        viewModelScope.launch {
            deleteUseCase(_state.value.groupId).collect { result ->
                if (result is DataState.Success) _navigateBack.emit(Unit)
            }
        }
    }

    private fun exitGroup() {
        viewModelScope.launch {
            exitUseCase(_state.value.groupId).collect { result ->
                if (result is DataState.Success) _navigateBack.emit(Unit)
            }
        }
    }
}
