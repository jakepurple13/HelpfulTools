package com.programmersbox.helpfulutils

import android.content.Context
import android.content.SharedPreferences

private var sharedPrefName: String = "Prefs"

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