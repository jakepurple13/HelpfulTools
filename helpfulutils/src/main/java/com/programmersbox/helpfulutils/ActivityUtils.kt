package com.programmersbox.helpfulutils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.view.View
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent

/**
 * Super easy way to request permissions
 */
fun ComponentActivity.requestPermissions(vararg permissions: String, onResult: (PermissionInfo) -> Unit) {
    val perm = checkSelfPermissions(*permissions)
    if (perm.isGranted) onResult(perm)
    else {
        val observer = PermissionWatcher()
        var onPausedCalledAt = -1L
        observer.onResumeCallback = {
            val responseTime = System.currentTimeMillis() - onPausedCalledAt
            if (onPausedCalledAt > 0 && responseTime < 250) {
                startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", packageName, null)))
            } else {
                observer.onResumeCallback = null
                lifecycle.removeObserver(observer)
                onResult(checkSelfPermissions(*permissions))
            }
        }
        observer.onPauseCallback = {
            onPausedCalledAt = System.currentTimeMillis()
            observer.onPauseCallback = null
        }
        lifecycle.addObserver(observer)
        ActivityCompat.requestPermissions(this, permissions, 90)
    }
}

fun Context.checkSelfPermissions(vararg permissions: String): PermissionInfo =
    permissions.partition { ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED }
        .let { PermissionInfo(it.second.isEmpty(), it.first, it.second) }

data class PermissionInfo internal constructor(val isGranted: Boolean, val grantedPermissions: List<String>, val deniedPermissions: List<String>)

private class PermissionWatcher : LifecycleObserver {
    var onResumeCallback: ((Boolean) -> Unit)? = null
    var onPauseCallback: ((Boolean) -> Unit)? = null
    var readyToCheck = false

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
        readyToCheck = true
        onPauseCallback?.invoke(readyToCheck)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        if (readyToCheck) onResumeCallback?.invoke(readyToCheck)
        readyToCheck = false
    }
}

/**
 * Enters ImmersiveMode
 */
fun ComponentActivity.enableImmersiveMode() {
    val window = window
    window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
    window.decorView.setOnSystemUiVisibilityChangeListener { visibility ->
        if (visibility != 0) return@setOnSystemUiVisibilityChangeListener

        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN).let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                it or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            } else it
        }
    }
}

/**
 * This snippet hides the system bars.
 * Set the content to appear under the system bars so that the content doesn't resize when the system bars hide and show.
 * Set the IMMERSIVE flag.
 */
fun ComponentActivity.hideSystemUI() {
    window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION //nav bar
            or View.SYSTEM_UI_FLAG_FULLSCREEN //status bar
            ).let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                it or View.SYSTEM_UI_FLAG_IMMERSIVE
            } else it
        }
}

/**
 * This snippet shows the system bars. It does this by removing all the flags
 * except for the ones that make the content appear under the system bars.
 */
fun ComponentActivity.showSystemUI() {
    window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
}

/**
 * Adds a secure flag so that screenshots cannot be taken or viewed on non-secure displays
 * @see WindowManager.LayoutParams.FLAG_SECURE
 */
fun ComponentActivity.addSecureFlag() {
    window?.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
}

/**
 * Removes the secure flag
 * @see WindowManager.LayoutParams.FLAG_SECURE
 */
fun ComponentActivity.clearSecureFlag() {
    window?.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
}

enum class ThemeSetting(val type: Int) {
    SYSTEM(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM),
    LIGHT(AppCompatDelegate.MODE_NIGHT_NO),
    NIGHT(AppCompatDelegate.MODE_NIGHT_YES);

    companion object {
        val currentThemeSetting get() = values().find { it.type == AppCompatDelegate.getDefaultNightMode() }
        fun setTheme(setting: ThemeSetting) = AppCompatDelegate.setDefaultNightMode(setting.type)
    }
}
