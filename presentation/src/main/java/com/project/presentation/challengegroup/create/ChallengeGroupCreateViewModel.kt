package com.project.presentation.challengegroup.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.domain.model.DataState
import com.project.domain.usecase.challengegroup.CreateChallengeGroupUseCase
import com.project.domain.usecase.challengegroup.EnrollChallengeGroupUseCase
import com.project.presentation.challengegroup.create.ChallengeGroupCreateState.Companion.MAX_HASHTAGS
import com.project.presentation.challengegroup.create.ChallengeGroupCreateState.Companion.MAX_PARTICIPANTS
import com.project.presentation.challengegroup.create.ChallengeGroupCreateState.Companion.MIN_PARTICIPANTS
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
class ChallengeGroupCreateViewModel @Inject constructor(
    private val createUseCase: CreateChallengeGroupUseCase,
    private val enrollUseCase: EnrollChallengeGroupUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ChallengeGroupCreateState())
    val state: StateFlow<ChallengeGroupCreateState> = _state.asStateFlow()

    private val _navigateBack = MutableSharedFlow<Unit>()
    val navigateBack = _navigateBack.asSharedFlow()

    fun onEvent(event: ChallengeGroupCreateEvent) {
        when (event) {
            is ChallengeGroupCreateEvent.ChangeTitle ->
                _state.update { it.copy(title = event.value) }

            is ChallengeGroupCreateEvent.ChangeReward ->
                _state.update { it.copy(reward = event.value.filter { c -> c.isDigit() }) }

            is ChallengeGroupCreateEvent.ChangeHashtagInput ->
                _state.update { it.copy(hashtagInput = event.value) }

            is ChallengeGroupCreateEvent.AddHashtag -> addHashtag()
            is ChallengeGroupCreateEvent.RemoveHashtag ->
                _state.update { it.copy(hashtags = it.hashtags - event.tag) }

            is ChallengeGroupCreateEvent.OpenCategorySheet ->
                _state.update { it.copy(isCategorySheetOpen = true) }
            is ChallengeGroupCreateEvent.CloseCategorySheet ->
                _state.update { it.copy(isCategorySheetOpen = false) }
            is ChallengeGroupCreateEvent.SelectCategory ->
                _state.update { it.copy(category = event.value, isCategorySheetOpen = false) }

            is ChallengeGroupCreateEvent.IncreaseMaxSize ->
                _state.update { if (it.maxSize < MAX_PARTICIPANTS) it.copy(maxSize = it.maxSize + 1) else it }
            is ChallengeGroupCreateEvent.DecreaseMaxSize ->
                _state.update { if (it.maxSize > MIN_PARTICIPANTS) it.copy(maxSize = it.maxSize - 1) else it }

            is ChallengeGroupCreateEvent.ToggleHidden ->
                _state.update { it.copy(isHidden = event.value, password = if (!event.value) "" else it.password) }
            is ChallengeGroupCreateEvent.ChangePassword ->
                _state.update { it.copy(password = event.value) }

            is ChallengeGroupCreateEvent.Submit -> submit()
        }
    }

    private fun addHashtag() {
        val s = _state.value
        if (!s.canAddHashtag) return
        val tag = s.hashtagInput.trimStart('#').trim()
        if (tag.isBlank() || s.hashtags.contains(tag)) {
            _state.update { it.copy(hashtagInput = "") }
            return
        }
        _state.update {
            it.copy(
                hashtags = (it.hashtags + tag).take(MAX_HASHTAGS),
                hashtagInput = ""
            )
        }
    }

    private fun submit() {
        val s = _state.value
        if (!s.isValid) return
        val password = if (s.isHidden) s.password else null
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            createUseCase(
                title = s.title.trim(),
                reward = s.rewardInt ?: 0,
                hashtags = s.hashtags,
                category = s.category.orEmpty(),
                maxSize = s.maxSize,
                isHidden = s.isHidden,
                password = password
            ).collect { result ->
                when (result) {
                    is DataState.Success -> {
                        val newGroupId = result.data?.id
                        if (newGroupId != null) {
                            enrollNewlyCreated(newGroupId, password)
                        } else {
                            _state.update { it.copy(isLoading = false, isSuccess = true) }
                            _navigateBack.emit(Unit)
                        }
                    }
                    is DataState.Error ->
                        _state.update { it.copy(isLoading = false, errorMessage = result.message) }
                    else -> _state.update { it.copy(isLoading = false) }
                }
            }
        }
    }

    /** 생성 직후 본인이 자동으로 참여 처리. enroll 결과와 무관하게 화면은 닫고 메인을 새로고침하도록 함 */
    private suspend fun enrollNewlyCreated(groupId: Int, password: String?) {
        enrollUseCase(groupId = groupId, password = password).collect { result ->
            when (result) {
                is DataState.Success -> {
                    _state.update { it.copy(isLoading = false, isSuccess = true) }
                    _navigateBack.emit(Unit)
                }
                is DataState.Error -> {
                    _state.update { it.copy(isLoading = false, isSuccess = true, errorMessage = result.message) }
                    _navigateBack.emit(Unit)
                }
                is DataState.Exception -> {
                    _state.update { it.copy(isLoading = false, isSuccess = true, errorMessage = result.e.message) }
                    _navigateBack.emit(Unit)
                }
                is DataState.Loading -> Unit
            }
        }
    }
}
