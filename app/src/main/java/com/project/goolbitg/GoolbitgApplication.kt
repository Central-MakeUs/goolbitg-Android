package com.project.goolbitg

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class GoolbitgApplication: Application() {
    override fun onCreate() {
        super.onCreate()
    }
}