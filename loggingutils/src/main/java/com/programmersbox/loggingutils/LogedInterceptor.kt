package com.programmersbox.loggingutils

import android.util.Log

interface LogedInterceptor {
    fun log(level: LogLevel, tag: String, msg: String)
}

enum class LogLevel {
    WARN, ASSERT, INFO, VERBOSE, ERROR, DEBUG;

    companion object {
        internal operator fun invoke(level: Int) = when (level) {
            Log.WARN -> WARN
            Log.ASSERT -> ASSERT
            Log.INFO -> INFO
            Log.VERBOSE -> VERBOSE
            Log.ERROR -> ERROR
            Log.DEBUG -> DEBUG
            else -> INFO
        }
    }
}
