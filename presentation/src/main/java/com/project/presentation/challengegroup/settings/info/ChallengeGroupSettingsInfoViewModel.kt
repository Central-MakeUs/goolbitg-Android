package com.project.presentation.challengegroup.settings.info

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.domain.model.DataState
import com.project.domain.usecase.challengegroup.FetchChallengeGroupDetailUseCase
import com.project.domain.usecase.challengegroup.UpdateChallengeGroupUseCase
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
class ChallengeGroupSettingsInfoViewModel @Inject constructor(
    private val fetchDetailUseCase: FetchChallengeGroupDetailUseCase,
    private val updateUseCase: UpdateChallengeGroupUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ChallengeGroupSettingsInfoState())
    val state: StateFlow<ChallengeGroupSettingsInfoState> = _state.asStateFlow()

    private val _navigateBack = MutableSharedFlow<Unit>()
    val navigateBack = _navigateBack.asSharedFlow()

    fun onEvent(event: ChallengeGroupSettingsInfoEvent) {
        when (event) {
            is ChallengeGroupSettingsInfoEvent.InitGroupId -> {
                _state.update { it.copy(groupId = event.groupId) }
                loadGroup(event.groupId)
            }
            is ChallengeGroupSettingsInfoEvent.ChangeTitle -> _state.update { it.copy(title = event.value) }
            is ChallengeGroupSettingsInfoEvent.ChangeReward -> _state.update { it.copy(reward = event.value) }
            is ChallengeGroupSettingsInfoEvent.ChangeHashtags -> _state.update { it.copy(hashtags = event.value) }
            is ChallengeGroupSettingsInfoEvent.ChangeCategory -> _state.update { it.copy(category = event.value) }
            is ChallengeGroupSettingsInfoEvent.ChangeMaxSize -> _state.update { it.copy(maxSize = event.value) }
            is ChallengeGroupSettingsInfoEvent.ToggleHidden -> _state.update { it.copy(isHidden = event.value) }
            is ChallengeGroupSettingsInfoEvent.ChangePassword -> _state.update { it.copy(password = event.value) }
            is ChallengeGroupSettingsInfoEvent.SaveChanges -> saveChanges()
        }
    }

    private fun loadGroup(groupId: Int) {
        viewModelScope.launch {
            fetchDetailUseCase(groupId).collect { result ->
                if (result is DataState.Success) {
                    val group = result.data?.group ?: return@collect
                    _state.update {
                        it.copy(
                            group = group,
                            title = group.title,
                            reward = group.reward.toString(),
                            hashtags = group.hashtags.joinToString(" ") { h -> "#$h" },
                            category = group.category,
                            maxSize = group.maxSize.toString(),
                            isHidden = group.isHidden,
                            password = group.password ?: ""
                        )
                    }
                }
            }
        }
    }

    private fun saveChanges() {
        val s = _state.value
        if (!s.isValid) return
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val hashtags = s.hashtags.split(" ", ",").map { it.trimStart('#').trim() }.filter { it.isNotEmpty() }
            updateUseCase(
                groupId = s.groupId,
                title = s.title,
                reward = s.reward.toIntOrNull() ?: 0,
                hashtags = hashtags,
                category = s.category,
                maxSize = s.maxSize.toIntOrNull() ?: 10,
                isHidden = s.isHidden,
                password = if (s.isHidden) s.password else null
            ).collect { result ->
                when (result) {
                    is DataState.Success -> {
                        _state.update { it.copy(isLoading = false) }
                        _navigateBack.emit(Unit)
                    }
                    is DataState.Error -> _state.update { it.copy(isLoading = false, errorMessage = result.message) }
                    else -> _state.update { it.copy(isLoading = false) }
                }
            }
        }
    }
}
