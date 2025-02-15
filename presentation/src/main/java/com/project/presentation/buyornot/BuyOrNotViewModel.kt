package com.project.presentation.buyornot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.domain.model.DataState
import com.project.domain.usecase.buyornot.FetchBuyOrNotPostsUseCase
import com.project.presentation.item.ReportReason
import com.project.presentation.item.ReportReasonEnum
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BuyOrNotViewModel @Inject constructor(
    private val fetchBuyOrNotPostsUseCase: FetchBuyOrNotPostsUseCase
) : ViewModel() {
    private val _state: MutableStateFlow<BuyOrNotState> = MutableStateFlow(BuyOrNotState())
    val state get() = _state

    init {
        fetchBuyOrNotPosts()
    }

    fun onEvent(event: BuyOrNotEvent) {
        when (event) {
            is BuyOrNotEvent.ClickReportReason -> {
                _state.value = state.value.copy(
                    reportList = ReportReasonEnum.entries.mapIndexed { idx, item ->
                        ReportReason(
                            reasonEnum = item,
                            isSelected = idx == event.idx
                        )
                    }
                )
            }
            is BuyOrNotEvent.FetchNextPage -> {
                fetchBuyOrNotPosts()
            }
        }
    }

    private fun fetchBuyOrNotPosts() {
        viewModelScope.launch {
            fetchBuyOrNotPostsUseCase(
                page = state.value.page,
                size = state.value.size
            ).collect { result ->
                when (result) {
                    is DataState.Success -> {
                        result.data?.let {
                            _state.value = state.value.copy(
                                page = state.value.page + 1,
                                buyOrNotPostList = state.value.buyOrNotPostList.plus(it.items).map { it }
                            )
                        }
                    }
                    is DataState.Loading -> {
                        _state.value = state.value.copy(
                            isLoading = result.isLoading
                        )
                    }
                    else -> Unit
                }
            }
        }
    }
}
