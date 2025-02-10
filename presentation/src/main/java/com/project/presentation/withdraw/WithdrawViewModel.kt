package com.project.presentation.withdraw

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.domain.model.DataState
import com.project.domain.usecase.auth.WithdrawAccountUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WithdrawViewModel @Inject constructor(
    private val withdrawAccountUseCase: WithdrawAccountUseCase
): ViewModel() {
    private val _state: MutableStateFlow<WithdrawState> = MutableStateFlow(WithdrawState.create())
    val state get() = _state.asStateFlow()

    fun withdrawAccount(reason: String){
        viewModelScope.launch {
            withdrawAccountUseCase(reason = reason).collect{ result ->
                when(result){
                    is DataState.Loading -> {
                        _state.value = state.value.copy(
                            isLoading = result.isLoading
                        )
                    }
                    is DataState.Success -> {
                        _state.value = state.value.copy(
                            isWithdrawSuccess = true
                        )
                    }
                    else -> Unit
                }
            }
        }
    }
}