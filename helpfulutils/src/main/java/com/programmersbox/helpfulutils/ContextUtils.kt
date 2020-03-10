package com.programmersbox.helpfulutils

import android.content.Context
import android.content.SharedPreferences
import android.os.Handler
import android.os.Looper

private var sharedPrefName: String = "HelpfulUtils"

/**
 * A name for shared preferences
 */
var Context.defaultSharedPrefName: String
    get() = sharedPrefName
    set(value) = run { sharedPrefName = value }

/**
 * A default shared preferences
 */
val Context.defaultSharedPref: SharedPreferences get() = getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE)

/**
 * A fun little method to always be able to run on the ui thread
 */
fun runOnUIThread(runnable: () -> Unit) = Handler(Looper.getMainLooper()).post(runnable)
