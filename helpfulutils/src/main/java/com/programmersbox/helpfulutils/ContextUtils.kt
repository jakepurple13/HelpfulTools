package com.programmersbox.helpfulutils

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.AudioManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.annotation.RequiresApi
import java.io.Serializable

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
fun SharedPreferences.Editor.put(vararg pairs: Pair<String, Any>) = apply {
    pairs.forEach {
        val key = it.first
        @Suppress("UNCHECKED_CAST")
        when (val s = it.second) {
            is Boolean -> putBoolean(key, s)
            is Float -> putFloat(key, s)
            is Int -> putInt(key, s)
            is Long -> putLong(key, s)
            is String -> putString(key, s)
            is Set<*> -> putStringSet(key, s as Set<String>)
        }
    }
}

/**
 * A fun little method to always be able to run on the ui thread
 */
fun runOnUIThread(runnable: () -> Unit) = Handler(Looper.getMainLooper()).post(runnable)

/**
 * An easy way to put data into an intent and start an activity
 */
inline fun <reified T> Context.startActivity(vararg pairs: Pair<String, Any>) = startActivity(Intent(this, T::class.java).putExtras(*pairs))

/**
 * An easy way to put data into an intent
 */
fun Intent.putExtras(vararg pairs: Pair<String, Any>) = apply { pairs.forEach { putExtra(it.first, it.second as Serializable) } }

val Context.audioManager get() = getSystemService(Context.AUDIO_SERVICE) as AudioManager

enum class AudioStreamTypes(internal var type: Int) {
    STREAM_VOICE_CALL(AudioManager.STREAM_VOICE_CALL),
    STREAM_SYSTEM(AudioManager.STREAM_SYSTEM),
    STREAM_RING(AudioManager.STREAM_RING),
    STREAM_MUSIC(AudioManager.STREAM_MUSIC),
    STREAM_ALARM(AudioManager.STREAM_ALARM),
    STREAM_NOTIFICATION(AudioManager.STREAM_NOTIFICATION),
    STREAM_DTMF(AudioManager.STREAM_DTMF),

    @RequiresApi(Build.VERSION_CODES.O)
    STREAM_ACCESSIBILITY(AudioManager.STREAM_ACCESSIBILITY);

    fun setVolume(context: Context, value: Int) = context.setStreamVolume(value, this)
    fun getVolume(context: Context) = context.getStreamVolume(this)
}

/**
 * Set the volume
 * @param value - the volume level
 * @param type - The category of the type of stream. Default is [AudioStreamTypes.STREAM_MUSIC]
 */
fun Context.setStreamVolume(value: Int, type: AudioStreamTypes = AudioStreamTypes.STREAM_MUSIC) = audioManager.setStreamVolume(type.type, value, 0)

/**
 * Get the volume
 * @param type - The category of the type of stream. Default is [AudioStreamTypes.STREAM_MUSIC]
 */
fun Context.getStreamVolume(type: AudioStreamTypes = AudioStreamTypes.STREAM_MUSIC) = audioManager.getStreamVolume(type.type)
