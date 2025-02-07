package com.project.presentation.login

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.project.data.preferences.AuthDataStore
import com.project.domain.common.ErrorCode
import com.project.domain.model.DataState
import com.project.domain.usecase.auth.LoginUseCase
import com.project.domain.usecase.auth.RegisterUseCase
import com.project.domain.usecase.user.CheckRegisterStatusUseCase
import com.project.domain.usecase.user.GetUserInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authDataStore: AuthDataStore,
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val checkRegisterStatusUseCase: CheckRegisterStatusUseCase,
    private val getUserInfoUseCase: GetUserInfoUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(LoginState.create())
    val state get() = _state.asStateFlow()

    private val isPermission = authDataStore.getIsPermissionFlow()

    private val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
        if (error != null) {
            Log.e(TAG, "카카오계정으로 로그인 실패", error)
        } else if (token != null) {
            if (token.idToken != null) {
                socialLogin(idToken = token.idToken!!)
            }
        }
    }

    fun loginWithKakaoTalk(context: Context) {
        // 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
            UserApiClient.instance.loginWithKakaoTalk(context) { token, error ->
                if (error != null) {
                    Log.e(TAG, "카카오톡으로 로그인 실패", error)

                    // 사용자가 카카오톡 설치 후 디바이스 권한 요청 화면에서 로그인을 취소한 경우,
                    // 의도적인 로그인 취소로 보고 카카오계정으로 로그인 시도 없이 로그인 취소로 처리 (예: 뒤로 가기)
                    if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                        return@loginWithKakaoTalk
                    }

                    // 카카오톡에 연결된 카카오계정이 없는 경우, 카카오계정으로 로그인 시도
                    UserApiClient.instance.loginWithKakaoAccount(
                        context,
                        callback = callback
                    )
                } else if (token != null) {
                    if (token.idToken != null) {
                        socialLogin(idToken = token.idToken!!)
                    }
                }
            }
        } else {
            UserApiClient.instance.loginWithKakaoAccount(
                context,
                callback = callback
            )
        }
    }

    /**
     * 소셜 로그인을 수행한다.
     */
    private fun socialLogin(idToken: String) {
        viewModelScope.launch {
            loginUseCase(type = state.value.loginType, idToken = idToken).collect { result ->
                when (result) {
                    is DataState.Loading -> {
                        _state.value = _state.value.copy(
                            isLoading = result.isLoading
                        )
                    }
                    is DataState.Success -> {
                        if (result.data != null) {
                            authDataStore.setAccessToken(accessToken = result.data!!.accessToken)
                            authDataStore.setRefreshToken(refreshToken = result.data!!.refreshToken)
                            checkPermissionFlow()
                        }
                    }
                    is DataState.Error -> {
                        if (result.code == ErrorCode.NotRegisteredUser.code) {
                            register(idToken = idToken)
                        }
                    }
                    is DataState.Exception -> {

                    }
                }
            }
        }
    }

    /**
     * 회원가입을 수행한다
     */
    private fun register(idToken: String) {
        viewModelScope.launch {
            registerUseCase(type = state.value.loginType, idToken = idToken).collect { result ->
                when (result) {
                    is DataState.Loading -> {
                        _state.value = _state.value.copy(
                            isLoading = result.isLoading
                        )
                    }
                    is DataState.Success -> {
                        if (result.data == true) {
                            socialLogin(idToken = idToken)
                        }
                    }
                    is DataState.Error -> {

                    }
                    is DataState.Exception -> {

                    }
                }
            }
        }
    }

    /**
     * 권한 화면 진입 여부 확인
     */
    private fun checkPermissionFlow() {
        viewModelScope.launch {
            val isPermissionFlow = isPermission.firstOrNull()
            if (isPermissionFlow == true) {
                authDataStore.setIsPermissionFlow(isPermissionFlow = false)
                _state.value = _state.value.copy(
                    isPermission = true
                )
            } else {
                checkRegisterStatus()
            }
        }
    }

    /**
     * 온보딩 입력 플로우 체크
     */
    fun checkRegisterStatus() {
        viewModelScope.launch {
            checkRegisterStatusUseCase().collect { result ->
                when (result) {
                    is DataState.Loading -> {
                        _state.value = _state.value.copy(
                            isLoading = result.isLoading
                        )
                    }
                    is DataState.Success -> {
                        getUserInfo()
                        withContext(Dispatchers.Default){
                            _state.value = _state.value.copy(
                                registerStatus = result.data?.status
                            )
                        }
                    }
                    else -> {

                    }
                }
            }
        }
    }

    private fun getUserInfo(){
        viewModelScope.launch {
            getUserInfoUseCase().collect{ result ->
                when(result){
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
}
