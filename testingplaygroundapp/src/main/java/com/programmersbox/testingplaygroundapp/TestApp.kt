package com.programmersbox.testingplaygroundapp

import android.app.Application
import com.programmersbox.loggingutils.Loged

class TestApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Loged.FILTER_BY_PACKAGE_NAME = "programmersbox"
        Loged.TAG = "TestApp"
    }
}