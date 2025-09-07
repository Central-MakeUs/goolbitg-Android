package com.project.presentation.challenge.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.domain.model.DataState
import com.project.domain.model.challenge.ChallengeGroupItemModel
import com.project.domain.usecase.challenge.FetchChallengeGroupsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChallengeGroupViewModel @Inject constructor(
    private val fetchChallengeGroupsUseCase: FetchChallengeGroupsUseCase
) : ViewModel() {

    private val _challengeGroups = MutableStateFlow<List<ChallengeGroupItemModel>?>(null)
    val challengeGroups get() = _challengeGroups.asStateFlow()

    init {
        fetchChallengeGroups()
    }

    // 챌린지 그룹 목록을 가져온다
    private fun fetchChallengeGroups() {
        viewModelScope.launch {
            fetchChallengeGroupsUseCase().collectLatest { result ->
                when(result) {
                    is DataState.Success -> {
                        _challengeGroups.value = result.data ?: emptyList()
                    }
                    else -> {

                    }
                }
            }
        }
    }
}
