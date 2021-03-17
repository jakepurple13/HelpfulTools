package com.programmersbox.loggingutils

import android.util.Log

fun interface LogedInterceptor {
    /**
     * The log interceptor
     * @param level The level of the log.
     * @param tag The tag.
     * @param msg The msg.
     * @return true if you want the log to be printed to the console, false if you don't
     * @see LogLevel
     */
    fun log(level: LogLevel, tag: String, msg: String): Boolean
}

enum class LogLevel {
    WARN, ASSERT, INFO, VERBOSE, ERROR, DEBUG;

    companion object {
        operator fun invoke(level: Int) = when (level) {
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
