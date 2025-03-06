package com.project.presentation.buyornot.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.domain.common.ErrorCode
import com.project.domain.model.DataState
import com.project.domain.usecase.buyornot.DeleteBuyOrNotPostingUseCase
import com.project.domain.usecase.buyornot.FetchBuyOrNotPostsUseCase
import com.project.domain.usecase.buyornot.ReportPostingUseCase
import com.project.domain.usecase.buyornot.VotePostingUseCase
import com.project.presentation.item.ReportReason
import com.project.presentation.item.ReportReasonEnum
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BuyOrNotViewModel @Inject constructor(
    private val fetchBuyOrNotPostsUseCase: FetchBuyOrNotPostsUseCase,
    private val deleteBuyOrNotPostingUseCase: DeleteBuyOrNotPostingUseCase,
    private val votePostingUseCase: VotePostingUseCase,
    private val reportPostingUseCase: ReportPostingUseCase
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
            is BuyOrNotEvent.VotePosting -> {
                votePosting(event.postId, event.isGood)
            }
            is BuyOrNotEvent.ModifyLocalPosting -> {
                _state.value = state.value.copy(
                    mainPostList = state.value.mainPostList?.map {
                        if (it.id == event.postId) {
                            it.copy(
                                productName = event.productName,
                                productPrice = event.price,
                                productImageUrl = event.imgUrl,
                                goodReason = event.goodReason,
                                badReason = event.badReason,
                            )
                        } else {
                            it
                        }
                    },
                    myPostList = state.value.myPostList?.map {
                        if (it.id == event.postId) {
                            it.copy(
                                productName = event.productName,
                                productPrice = event.price,
                                productImageUrl = event.imgUrl,
                                goodReason = event.goodReason,
                                badReason = event.badReason,
                            )
                        } else {
                            it
                        }
                    }
                )
            }
            is BuyOrNotEvent.ReportPosting -> {
                initReportList()
                reportPosting(postId = event.postId, reason = event.reason)
            }
        }
    }

    fun initReportResult(){
        viewModelScope.launch {
            _state.value = state.value.copy(
                reportResult = null
            )
        }
    }

    fun initReportList(){
        _state.value= state.value.copy(
            reportList = ReportReasonEnum.entries.map { ReportReason(reasonEnum = it) }
        )
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
                                mainPostList = state.value.mainPostList?.plus(it.items)?.map { it } ?: it.items
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

    private fun fetchMyBuyOrNotPosts() {
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
                                myPostList = state.value.myPostList?.plus(it.items)?.map { it } ?: it.items
                            )
                        }
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun deleteMyPosting(postId: Int) {
        viewModelScope.launch {
            deleteBuyOrNotPostingUseCase(postId = postId).collect { result ->
                when (result) {
                    is DataState.Loading -> {
                        _state.value = state.value.copy(
                            isMyPostsLoading = result.isLoading
                        )
                    }
                    is DataState.Success -> {
                        val target = state.value.myPostList?.find { it.id == postId }
                        if (target != null) {
                            _state.value = state.value.copy(
                                myPostList = state.value.myPostList?.toMutableList().apply {
                                    this?.removeIf { it.id == postId }
                                }
                            )
                        }
                    }
                    else -> Unit
                }
            }
        }

    }

    private fun votePosting(postId: Int, isGood: Boolean) {
        viewModelScope.launch {
            val voteStr = if (isGood) "GOOD" else "BAD"
            votePostingUseCase(postId = postId, vote = voteStr).collect { result ->
                when (result) {
                    is DataState.Loading -> {
                        _state.value = state.value.copy(
                            isMainPostsLoading = result.isLoading
                        )
                    }
                    is DataState.Success -> {
                        result.data?.let { voteResult ->
                            _state.value = state.value.copy(
                                mainPostList = state.value.mainPostList?.map {
                                    if (it.id == postId) {
                                        it.copy(
                                            goodVoteCount = voteResult.goodVoteCount,
                                            badVoteCount = voteResult.badVoteCount
                                        )
                                    } else {
                                        it
                                    }
                                },
                                myPostList = state.value.myPostList?.map {
                                    if (it.id == postId) {
                                        it.copy(
                                            goodVoteCount = voteResult.goodVoteCount,
                                            badVoteCount = voteResult.badVoteCount
                                        )
                                    } else {
                                        it
                                    }
                                },
                            )
                        }
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun reportPosting(postId: Int, reason: String){
        viewModelScope.launch {
            reportPostingUseCase(postId = postId, reason = reason).collect{ result ->
                when(result){
                    is DataState.Loading -> {
                        _state.value= state.value.copy(
                            isMainPostsLoading = result.isLoading
                        )
                    }
                    is DataState.Success -> {
                        _state.value = state.value.copy(
                            reportResult = "신고가 완료되었습니다."
                        )
                    }
                    is DataState.Error -> {
                        val msg = when(result.code){
                            ErrorCode.NotExistPosting.code -> ErrorCode.NotExistPosting.msg
                            ErrorCode.AlreadyReported.code -> ErrorCode.AlreadyReported.msg
                            else -> null
                        }
                        _state.value = state.value.copy(
                            reportResult = msg
                        )
                    }
                    else -> Unit
                }
            }
        }
    }
}
