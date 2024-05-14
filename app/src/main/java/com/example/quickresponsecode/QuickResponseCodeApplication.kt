package com.example.quickresponsecode

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class QuickResponseCodeApplication : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}