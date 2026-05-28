package com.project.presentation.challengegroup.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.domain.model.DataState
import com.project.domain.usecase.challengegroup.EnrollChallengeGroupUseCase
import com.project.domain.usecase.challengegroup.FetchChallengeGroupListUseCase
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
class ChallengeGroupSearchViewModel @Inject constructor(
    private val fetchListUseCase: FetchChallengeGroupListUseCase,
    private val enrollUseCase: EnrollChallengeGroupUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ChallengeGroupSearchState())
    val state: StateFlow<ChallengeGroupSearchState> = _state.asStateFlow()

    private val _navigateToDetail = MutableSharedFlow<Int>()
    val navigateToDetail = _navigateToDetail.asSharedFlow()

    fun onEvent(event: ChallengeGroupSearchEvent) {
        when (event) {
            is ChallengeGroupSearchEvent.ChangeQuery -> _state.update { it.copy(query = event.query) }
            is ChallengeGroupSearchEvent.Search -> search()
            is ChallengeGroupSearchEvent.SelectGroup -> _state.update {
                // 공개방/비밀방 모두 동일한 참여 다이얼로그를 노출
                it.copy(showJoinDialog = true, selectedGroup = event.group, passwordInput = "")
            }
            is ChallengeGroupSearchEvent.ChangePassword -> _state.update { it.copy(passwordInput = event.password) }
            is ChallengeGroupSearchEvent.ConfirmJoin -> {
                val group = _state.value.selectedGroup ?: return
                val pwd = if (group.isHidden) _state.value.passwordInput else null
                joinGroup(group.id, pwd)
            }
            is ChallengeGroupSearchEvent.DismissDialog -> _state.update {
                it.copy(showJoinDialog = false, selectedGroup = null, passwordInput = "")
            }
        }
    }

    private fun search() {
        val query = _state.value.query
        if (query.isBlank()) return
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            fetchListUseCase(search = query).collect { result ->
                when (result) {
                    is DataState.Success -> _state.update {
                        it.copy(isLoading = false, results = result.data?.items ?: emptyList())
                    }
                    is DataState.Error -> _state.update { it.copy(isLoading = false, errorMessage = result.message) }
                    else -> _state.update { it.copy(isLoading = false) }
                }
            }
        }
    }

    private fun joinGroup(groupId: Int, password: String?) {
        viewModelScope.launch {
            enrollUseCase(groupId, password).collect { result ->
                when (result) {
                    is DataState.Success -> {
                        _state.update { it.copy(showJoinDialog = false, selectedGroup = null, passwordInput = "") }
                        _navigateToDetail.emit(groupId)
                    }
                    is DataState.Error -> _state.update { it.copy(errorMessage = result.message) }
                    else -> {}
                }
            }
        }
    }
}
