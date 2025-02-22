package com.project.presentation.buyornot.add

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.domain.model.DataState
import com.project.domain.usecase.buyornot.ModifyBuyOrNotPostingUseCase
import com.project.domain.usecase.buyornot.PostBuyOrNotPostingUseCase
import com.project.domain.usecase.buyornot.UploadImgUseCase
import com.project.presentation.util.PhotoUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BuyOrNotAddViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val postBuyOrNotPostingUseCase: PostBuyOrNotPostingUseCase,
    private val modifyBuyOrNotPostingUseCase: ModifyBuyOrNotPostingUseCase,
    private val uploadImgUseCase: UploadImgUseCase
) : ViewModel() {
    private val _state: MutableStateFlow<BuyOrNotAddState> = MutableStateFlow(BuyOrNotAddState())
    val state get() = _state.asStateFlow()

    fun onEvent(event: BuyOrNotAddEvent) {
        when (event) {
            is BuyOrNotAddEvent.ChangePostId -> {
                _state.value = state.value.copy(
                    postId = event.postId
                )
            }
            is BuyOrNotAddEvent.ChangeImgUrl -> {
                _state.value = state.value.copy(
                    prevImgUrl = event.newValue
                )
            }
            is BuyOrNotAddEvent.ChangeProductName -> {
                _state.value = state.value.copy(
                    productName = event.newValue
                )
            }
            is BuyOrNotAddEvent.ChangePrice -> {
                _state.value = state.value.copy(
                    price = event.newValue
                )
            }
            is BuyOrNotAddEvent.ChangeGoodReason -> {
                _state.value = state.value.copy(
                    goodReason = event.newValue
                )
            }
            is BuyOrNotAddEvent.ChangeBadReason -> {
                _state.value = state.value.copy(
                    badReason = event.newValue
                )
            }
            is BuyOrNotAddEvent.ChangeCropUri -> {
                _state.value = state.value.copy(
                    currCroppedImgUri = event.uri
                )
            }
            is BuyOrNotAddEvent.InitImgUrl -> {
                _state.value = state.value.copy(
                    prevImgUrl = null,
                    currCroppedImgUri = null,
                )
            }
            is BuyOrNotAddEvent.RequestAddPosting -> {
                if(state.value.postId != null){
                    if (!state.value.currCroppedImgUri.isNullOrEmpty()) {
                        requestModifyPostingWithUpload()
                    } else {
                        if (!state.value.prevImgUrl.isNullOrEmpty()) {
                            requestModifyPosting(imgUrl = state.value.prevImgUrl!!)
                        }
                    }
                }else{
                    if (!state.value.currCroppedImgUri.isNullOrEmpty()) {
                        requestAddPostingWithUpload()
                    } else {
                        if (!state.value.prevImgUrl.isNullOrEmpty()) {
                            requestAddPosting(imgUrl = state.value.prevImgUrl!!)
                        }
                    }
                }
            }
        }
    }

    fun setTabIdx(idx: Int){
        _state.value = state.value.copy(
            tabIdx = idx
        )
    }

    private fun requestAddPosting(imgUrl: String) {
        viewModelScope.launch {
            state.value.apply {
                if (checkState()) {
                    postBuyOrNotPostingUseCase(
                        productName = productName,
                        productPrice = price.toInt(),
                        productImageUrl = imgUrl,
                        goodReason = goodReason,
                        badReason = badReason,
                    ).collect { result ->
                        when (result) {
                            is DataState.Loading -> {
                                _state.value = state.value.copy(
                                    isLoading = result.isLoading
                                )
                            }
                            is DataState.Success -> {
                                _state.value = state.value.copy(
                                    isPostingSuccess = true
                                )
                            }
                            else -> Unit
                        }
                    }
                }
            }
        }
    }

    private fun requestAddPostingWithUpload() {
        viewModelScope.launch {
            state.value.apply {
                val uri = Uri.parse(currCroppedImgUri)
                val bitmap = PhotoUtil.decodeBitmapFromUri(context, uri)
                val byteArray = if (bitmap != null) {
                    PhotoUtil.bitmapToByteArray(bitmap) ?: byteArrayOf()
                } else {
                    byteArrayOf()
                }
                uploadImgUseCase(byteArray = byteArray).collect { result ->
                    when (result) {
                        is DataState.Loading -> {
                            _state.value = state.value.copy(
                                isLoading = false
                            )
                        }
                        is DataState.Success -> {
                            result.data?.let {
                                requestAddPosting(imgUrl = it)
                            }
                        }
                        else -> Unit
                    }
                }
            }
        }
    }

    private fun requestModifyPosting(imgUrl: String) {
        viewModelScope.launch {
            _state.value = state.value.copy(
                resultImg = imgUrl
            )
            state.value.apply {
                if (checkState()) {
                    if(postId != null){
                        modifyBuyOrNotPostingUseCase(
                            postId = postId,
                            productName = productName,
                            productPrice = price.toInt(),
                            productImageUrl = imgUrl,
                            goodReason = goodReason,
                            badReason = badReason,
                        ).collect { result ->
                            when (result) {
                                is DataState.Loading -> {
                                    _state.value = state.value.copy(
                                        isLoading = result.isLoading
                                    )
                                }
                                is DataState.Success -> {
                                    _state.value = state.value.copy(
                                        isPostingSuccess = true
                                    )
                                }
                                else -> Unit
                            }
                        }
                    }
                }
            }
        }
    }

    private fun requestModifyPostingWithUpload() {
        viewModelScope.launch {
            state.value.apply {
                val uri = Uri.parse(currCroppedImgUri)
                val bitmap = PhotoUtil.decodeBitmapFromUri(context, uri)
                val byteArray = if (bitmap != null) {
                    PhotoUtil.bitmapToByteArray(bitmap) ?: byteArrayOf()
                } else {
                    byteArrayOf()
                }
                uploadImgUseCase(byteArray = byteArray).collect { result ->
                    when (result) {
                        is DataState.Loading -> {
                            _state.value = state.value.copy(
                                isLoading = false
                            )
                        }
                        is DataState.Success -> {
                            result.data?.let {
                                requestModifyPosting(imgUrl = it)
                            }
                        }
                        else -> Unit
                    }
                }
            }
        }
    }
}

