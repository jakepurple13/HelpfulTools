package com.programmersbox.testingplaygroundapp

import android.app.Application
import com.programmersbox.loggingutils.LogLevel
import com.programmersbox.loggingutils.Loged
import com.programmersbox.loggingutils.LogedInterceptor

class TestApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Loged.FILTER_BY_PACKAGE_NAME = "programmersbox"
        Loged.TAG = "TestApp"
        Loged.logedInterceptor = Interceptor()
    }
}

class Interceptor : LogedInterceptor {
    override fun log(level: LogLevel, tag: String, msg: String) {
        //println("${level.name[0]}/$tag/$msg")
    }
}