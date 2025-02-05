package com.project.presentation.challenge.addition

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ChallengeAdditionViewModel @Inject constructor(

) : ViewModel() {
    private val _state: MutableStateFlow<ChallengeAdditionState> =
        MutableStateFlow(ChallengeAdditionState.create())
    val state get() = _state.asStateFlow()

    fun onEvent(event: ChallengeAdditionEvent){
        when(event){
            is ChallengeAdditionEvent.BackPressOption -> {
                _state.value = state.value.copy(
                    isBackEnabled = event.isBackEnabled
                )
            }
        }
    }

}