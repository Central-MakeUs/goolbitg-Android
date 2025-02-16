package com.project.presentation.buyornot

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.domain.model.DataState
import com.project.domain.usecase.buyornot.DeleteBuyOrNotPostingUseCase
import com.project.domain.usecase.buyornot.FetchBuyOrNotPostsUseCase
import com.project.presentation.item.ReportReason
import com.project.presentation.item.ReportReasonEnum
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BuyOrNotViewModel @Inject constructor(
    private val fetchBuyOrNotPostsUseCase: FetchBuyOrNotPostsUseCase,
    private val deleteBuyOrNotPostingUseCase: DeleteBuyOrNotPostingUseCase
) : ViewModel() {
    private val _state: MutableStateFlow<BuyOrNotState> = MutableStateFlow(BuyOrNotState())
    val state get() = _state

    init {
        fetchBuyOrNotPosts()
        fetchMyBuyOrNotPosts()
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
            is BuyOrNotEvent.FetchMainNextPage -> {
                fetchBuyOrNotPosts()
            }
            is BuyOrNotEvent.FetchMyNextPage -> {
                fetchMyBuyOrNotPosts()
            }
            is BuyOrNotEvent.DeleteMyPosting -> {
                deleteMyPosting(event.postId)
            }
        }
    }

    private fun fetchBuyOrNotPosts() {
        viewModelScope.launch {
            fetchBuyOrNotPostsUseCase(
                page = state.value.mainPostPage,
                size = state.value.mainPostSize
            ).collect { result ->
                when (result) {
                    is DataState.Success -> {
                        result.data?.let {
                            _state.value = state.value.copy(
                                mainPostPage = state.value.mainPostPage + 1,
                                mainPostList = state.value.mainPostList.plus(it.items).map { it }
                            )
                        }
                    }
                    is DataState.Loading -> {
                        _state.value = state.value.copy(
                            isMainPostsLoading = result.isLoading
                        )
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun fetchMyBuyOrNotPosts(){
        viewModelScope.launch {
            fetchBuyOrNotPostsUseCase(
                page = state.value.myPostPage,
                size = state.value.myPostSize,
                isCreated = true
            ).collect { result ->
                when (result) {
                    is DataState.Success -> {
                        result.data?.let {
                            _state.value = state.value.copy(
                                myPostPage = state.value.myPostPage + 1,
                                myPostList = state.value.myPostList.plus(it.items).map { it }
                            )
                        }
                    }
                    is DataState.Loading -> {
                        _state.value = state.value.copy(
                            isMyPostsLoading = result.isLoading
                        )
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun deleteMyPosting(postId: Int){
        viewModelScope.launch {
            deleteBuyOrNotPostingUseCase(postId = postId).collect{ result ->
                when(result){
                    is DataState.Loading -> {
                        _state.value = state.value.copy(
                            isMyPostsLoading = result.isLoading
                        )
                    }
                    is DataState.Success -> {
                        val target = state.value.myPostList.find { it.id == postId }
                        if(target != null){
                            _state.value = state.value.copy(
                                myPostList = state.value.myPostList.toMutableList().apply {
                                    this.removeIf { it.id == postId }
                                }
                            )
                        }
                    }
                    is DataState.Exception -> {
                        Log.d("TAG", "deleteMyPosting: ${result.e.stackTrace}")
                    }
                    else -> {
                        Log.d("TAG", "deleteMyPosting: ${result.code}")
                    }
                }
            }
        }

    }
}
