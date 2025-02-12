package com.project.presentation.mypage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.data.preferences.AuthDataStore
import com.project.domain.model.DataState
import com.project.domain.usecase.auth.LogoutUseCase
import com.project.domain.usecase.user.GetUserInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel @Inject constructor(
    private val authDataStore: AuthDataStore,
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val logoutUseCase: LogoutUseCase
): ViewModel() {
    private val _state: MutableStateFlow<MyPageState> = MutableStateFlow(MyPageState.create())
    val state get() = _state

    init {
        getUserInfo()
    }

    fun onEvent(event: MyPageEvent){
        when(event){
            is MyPageEvent.Logout -> {
                logout()
            }
            is MyPageEvent.RefreshLogoutState -> {
                _state.value = MyPageState.create()
            }
        }
    }

    fun getUserInfo(){
        viewModelScope.launch {
            getUserInfoUseCase().collect{ result ->
                when(result){
                    is DataState.Success -> {
                        result.data?.let{
                            _state.value = state.value.copy(
                                userInfoModel = it
                            )
                        }
                    }
                    else -> Unit
                }

            }
        }
    }

    private fun logout(){
        viewModelScope.launch {
            logoutUseCase().collect{ result ->
                when(result){
                    is DataState.Loading -> {
                        _state.value = state.value.copy(
                            isLoading = result.isLoading
                        )
                    }
                    is DataState.Success -> {
                        authDataStore.deleteAccountInfo()
                        withContext(Dispatchers.Default){
                            _state.value = state.value.copy(
                                isLogoutSuccess = true
                            )
                        }
                    }
                    else -> Unit
                }
            }
        }
    }
}