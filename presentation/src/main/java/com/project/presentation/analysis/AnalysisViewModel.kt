package com.project.presentation.analysis

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.domain.model.DataState
import com.project.domain.usecase.analysis.FetchAnalysisReportUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnalysisViewModel @Inject constructor(
    private val fetchReportUseCase: FetchAnalysisReportUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(AnalysisState())
    val state: StateFlow<AnalysisState> = _state.asStateFlow()

    init {
        loadReport()
    }

    private fun loadReport() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            fetchReportUseCase().collect { result ->
                when (result) {
                    is DataState.Success -> _state.update { it.copy(isLoading = false, report = result.data) }
                    is DataState.Error -> _state.update { it.copy(isLoading = false, errorMessage = result.message) }
                    else -> _state.update { it.copy(isLoading = false) }
                }
            }
        }
    }
}
