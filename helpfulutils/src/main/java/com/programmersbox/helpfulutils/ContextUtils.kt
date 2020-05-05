package com.programmersbox.helpfulutils

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Handler
import android.os.Looper
import java.io.Serializable
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

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
 * An easy [Pair] version to put data into [SharedPreferences]
 */
fun SharedPreferences.Editor.put(vararg pairs: Pair<String, Any?>) = apply {
    pairs.forEach {
        val key = it.first
        @Suppress("UNCHECKED_CAST")
        when (val s = it.second) {
            is Boolean -> putBoolean(key, s)
            is Float -> putFloat(key, s)
            is Int -> putInt(key, s)
            is Long -> putLong(key, s)
            is String -> putString(key, s)
            is Set<*> -> putStringSet(key, s as? Set<String>)
        }
    }
}

/**
 * An easy way to put a [value] into [SharedPreferences]
 */
operator fun SharedPreferences.set(key: String, value: Any?) = edit().put(key to value).apply()

/**
 * An easy way to get a value from [SharedPreferences]
 */
inline operator fun <reified T> SharedPreferences.get(key: String) = get<T>(key, null)

@Suppress("IMPLICIT_CAST_TO_ANY", "UNCHECKED_CAST")
inline fun <reified T> SharedPreferences.get(key: String, defaultValue: T? = null): T? = when (defaultValue) {
    is Boolean? -> getBoolean(key, defaultValue as Boolean)
    is Float? -> getFloat(key, defaultValue as Float)
    is Int? -> getInt(key, defaultValue as Int)
    is Long? -> getLong(key, defaultValue as Long)
    is String? -> getString(key, defaultValue)
    is Set<*>? -> getStringSet(key, defaultValue as? Set<String>)
    else -> defaultValue
} as? T

@Suppress("UNCHECKED_CAST")
class SharedPrefDelegate<T : Serializable> internal constructor(
    private val prefs: Context.() -> SharedPreferences,
    private val key: String?,
    private val defaultValue: T?
) : ReadWriteProperty<Context, T?> {
    private val keys: KProperty<*>.() -> String get() = { key ?: name }
    override operator fun getValue(thisRef: Context, property: KProperty<*>): T? = thisRef.prefs().all[property.keys()] as? T ?: defaultValue
    override operator fun setValue(thisRef: Context, property: KProperty<*>, value: T?) =
        thisRef.prefs().edit().also { pref -> if (value == null) pref.remove(property.keys()) else pref.put(property.keys() to value) }.apply()
}

/**
 * Use this when you want to store and retrieve values that will be placed in [SharedPreferences]
 * default [SharedPreferences] is [defaultSharedPref]
 */
fun <T : Serializable> sharedPrefDelegate(
    defaultValue: T? = null,
    key: String? = null,
    prefs: Context.() -> SharedPreferences = { defaultSharedPref }
) = SharedPrefDelegate(prefs, key, defaultValue)

/**
 * A fun little method to always be able to run on the ui thread
 */
fun runOnUIThread(runnable: () -> Unit) = Handler(Looper.getMainLooper()).post(runnable)

/**
 * An easy way to put data into an intent and start an activity
 */
inline fun <reified T> Context.startActivity(vararg pairs: Pair<String, Serializable>) = startActivity(Intent(this, T::class.java).putExtras(*pairs))

/**
 * An easy way to put data into an intent
 */
fun Intent.putExtras(vararg pairs: Pair<String, Serializable>) = apply { pairs.forEach { putExtra(it.first, it.second) } }
