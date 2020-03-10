package com.programmersbox.helpfulutils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.ComponentActivity
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