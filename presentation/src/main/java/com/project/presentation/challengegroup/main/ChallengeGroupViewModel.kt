package com.project.presentation.challengegroup.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.domain.model.DataState
import com.project.domain.usecase.challengegroup.FetchChallengeGroupListUseCase
import com.project.domain.usecase.user.GetUserInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChallengeGroupViewModel @Inject constructor(
    private val fetchChallengeGroupListUseCase: FetchChallengeGroupListUseCase,
    private val getUserInfoUseCase: GetUserInfoUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ChallengeGroupState())
    val state: StateFlow<ChallengeGroupState> = _state.asStateFlow()

    /** 첫 로드 완료 여부 — 이후 새로고침은 silent (스켈레톤 안 보이게) */
    private var firstLoadDone = false

    init {
        // 본인 ID 미리 가져오기 — "내가 만든 방만 보기" 필터링에 사용
        viewModelScope.launch {
            getUserInfoUseCase().collect { result ->
                if (result is DataState.Success) {
                    _state.update { it.copy(currentUserId = result.data?.id) }
                }
            }
        }
    }

    fun onEvent(event: ChallengeGroupEvent) {
        when (event) {
            is ChallengeGroupEvent.LoadGroupList -> fetchGroupList()
            is ChallengeGroupEvent.ToggleMyRoomOnly -> {
                // API 재호출 없이 클라이언트에서 isMyRoomOnly 토글만 → State.groupList getter 가 자동 필터링
                _state.update { it.copy(isMyRoomOnly = event.enabled) }
            }
            is ChallengeGroupEvent.ChangeSearchQuery -> {
                _state.update { it.copy(searchQuery = event.query) }
            }
            is ChallengeGroupEvent.Search -> fetchGroupList()
        }
    }

    /**
     * 그룹 목록 fetch. 첫 호출에만 isLoading=true 로 스켈레톤 노출하고,
     * 이후 호출(예: 다른 화면에서 돌아올 때 ON_RESUME)에는 silent refresh 한다.
     */
    private fun fetchGroupList() {
        val silent = firstLoadDone
        viewModelScope.launch {
            val s = _state.value
            fetchChallengeGroupListUseCase(
                search = s.searchQuery.ifBlank { null },
                participating = true,
                created = null
            ).collect { result ->
                when (result) {
                    is DataState.Success -> {
                        firstLoadDone = true
                        _state.update {
                            it.copy(
                                isLoading = false,
                                allGroups = result.data?.items ?: emptyList()
                            )
                        }
                    }
                    is DataState.Error -> _state.update {
                        it.copy(isLoading = false, errorMessage = result.message)
                    }
                    is DataState.Exception -> _state.update {
                        it.copy(isLoading = false, errorMessage = result.e.message)
                    }
                    is DataState.Loading -> {
                        // silent refresh 시 isLoading 토글하지 않음 → 스켈레톤 미노출
                        if (!silent) {
                            _state.update { it.copy(isLoading = result.isLoading) }
                        }
                    }
                }
            }
        }
    }
}
