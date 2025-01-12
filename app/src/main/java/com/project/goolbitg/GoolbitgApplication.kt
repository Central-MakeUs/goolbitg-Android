package com.project.goolbitg

import android.app.Application
import com.kakao.sdk.common.KakaoSdk
import com.project.presentation.BuildConfig
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class GoolbitgApplication: Application() {
    override fun onCreate() {
        super.onCreate()

        KakaoSdk.init(this, BuildConfig.KAKAO_CLIENT_KEY)
    }
}