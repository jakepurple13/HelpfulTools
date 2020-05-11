package com.programmersbox.helpfultools

import android.app.Application
import com.programmersbox.helpfulutils.createNotificationChannel
import com.programmersbox.helpfulutils.createNotificationGroup
import com.programmersbox.helpfulutils.defaultSharedPrefName
import com.programmersbox.loggingutils.LogLevel
import com.programmersbox.loggingutils.Loged
import com.programmersbox.loggingutils.LogedInterceptor

class TestApp : Application() {

    override fun onCreate() {
        super.onCreate()
        Loged.FILTER_BY_PACKAGE_NAME = "com.programmersbox.helpfultools"
        Loged.TAG = "HelpfulTools"
        Loged.logedInterceptor = Interceptor()
        defaultSharedPrefName = "DefaultPrefNameTest"
        createNotificationChannel("testChannel")
        createNotificationGroup("testGroup")
    }

}

class Interceptor : LogedInterceptor {
    override fun log(level: LogLevel, tag: String, msg: String) {
        println("${level.name[0]}/$tag/$msg")
    }
}