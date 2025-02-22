package com.project.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.data.preferences.AuthDataStore
import com.project.domain.model.DataState
import com.project.domain.model.user.RegisterStatus
import com.project.domain.usecase.auth.RefreshTokenUseCase
import com.project.domain.usecase.user.CheckRegisterStatusUseCase
import com.project.domain.usecase.user.GetUserInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authDataStore: AuthDataStore,
    private val refreshTokenUseCase: RefreshTokenUseCase,
    private val checkRegisterStatusUseCase: CheckRegisterStatusUseCase,
    private val getUserInfoUseCase: GetUserInfoUseCase
) : ViewModel() {

    private val _state: MutableStateFlow<SplashState> = MutableStateFlow(SplashState.create())
    val state get() = _state.asStateFlow()

    private val splashMilliSeconds = 2000L
    private val startTime = System.currentTimeMillis()

    init {
        refreshToken()
    }

    private fun refreshToken() {
        viewModelScope.launch {
            val refreshToken = authDataStore.getRefreshToken().firstOrNull()
            if (refreshToken != null) {
                refreshTokenUseCase(refreshToken = refreshToken).collect { result ->
                    when (result) {
                        is DataState.Loading -> {}
                        is DataState.Success -> {
                            if (result.data != null) {
                                authDataStore.setAccessToken(accessToken = result.data!!.accessToken)
                                authDataStore.setRefreshToken(refreshToken = result.data!!.refreshToken)
                                withContext(Dispatchers.Default) {
                                    checkRegisterStatus()
                                }
                            } else {
                                val elapsedTime = System.currentTimeMillis() - startTime
                                if(elapsedTime < splashMilliSeconds){
                                    delay(splashMilliSeconds - elapsedTime)
                                }
                                _state.value = _state.value.copy(
                                    registerStatus = RegisterStatus.Login
                                )
                            }
                        }

                        else -> {
                            val elapsedTime = System.currentTimeMillis() - startTime
                            if(elapsedTime < splashMilliSeconds){
                                delay(splashMilliSeconds - elapsedTime)
                            }
                            _state.value = _state.value.copy(
                                registerStatus = RegisterStatus.Login
                            )
                        }
                    }
                }
            } else {
                val elapsedTime = System.currentTimeMillis() - startTime
                if(elapsedTime < splashMilliSeconds){
                    delay(splashMilliSeconds - elapsedTime)
                }
                _state.value = _state.value.copy(
                    registerStatus = RegisterStatus.Login
                )
            }
        }
    }

    private fun checkRegisterStatus() {
        viewModelScope.launch {
            checkRegisterStatusUseCase().collect { result ->
                when (result) {
                    is DataState.Loading -> {}
                    is DataState.Success -> {
                        getUserInfo()
                        withContext(Dispatchers.Default) {
                            val elapsedTime = System.currentTimeMillis() - startTime
                            if(elapsedTime < splashMilliSeconds){
                                delay(splashMilliSeconds - elapsedTime)
                            }
                            _state.value = _state.value.copy(
                                registerStatus = result.data?.status
                            )
                        }
                    }

                    else -> {
                        _state.value = _state.value.copy(
                            registerStatus = RegisterStatus.Login
                        )
                    }
                }
            }
        }
    }

    private suspend fun getUserInfo() {
        getUserInfoUseCase().collect { result ->
            when (result) {
                is DataState.Success -> {
                    result.data?.let { model ->
                        model.nickname?.let { nickname ->
                            authDataStore.setNickname(nickname)
                        }
                    }
                }

                else -> Unit
            }
        }
    }
}
