package com.programmersbox.helpfultools

import android.app.Application
import com.programmersbox.helpfulutils.defaultSharedPrefName
import com.programmersbox.loggingutils.Loged

class TestApp : Application() {

    override fun onCreate() {
        super.onCreate()
        Loged.FILTER_BY_PACKAGE_NAME = "com.programmersbox.helpfultools"
        Loged.TAG = "HelpfulTools"
        defaultSharedPrefName = "DefaultPrefNameTest"
    }

}