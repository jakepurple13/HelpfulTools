package com.programmersbox.loggingutils

import android.util.Log

/**
 * Created by Jacob on 10/3/17.
 */
object Loged {
    /**
     * If there are any other classes that you do not want to show up
     */
    var OTHER_CLASS_FILTER: (String) -> Boolean = { true }

    /**
     * Will pretty print all log messages if true
     */
    var SHOW_PRETTY: Boolean = true

    /**
     * Will include the thread name in with the tag for all logs if true
     */
    var WITH_THREAD_NAME: Boolean = true

    /**
     * Makes this the name of your package to prevent unwanted logs
     */
    @JvmStatic
    var FILTER_BY_PACKAGE_NAME = ""

    /**
     * A log tag for all log messages using the [Loged] class
     * Default is "Loged"
     */
    @JvmStatic
    var TAG = "Loged"

    /**
     * Enable this if you are unit testing. This will do a normal [println] instead of a [Log.println] if true
     */
    @JvmStatic
    var UNIT_TESTING = false

    /**
     * If you want to add an interceptor to add the logs to a certain location, make a class that implements [LogedInterceptor]
     */
    var logedInterceptor: LogedInterceptor? = null

    /**
     * Converting the StackTraceElement into a string that will let us click straight to it
     */
    private val stackToString: StackTraceElement.() -> String = { "${className}.${methodName}(${fileName}:${lineNumber})" }

    /**
     * Filtering out the classes we do not want to see
     */
    private val filter: (StackTraceElement) -> Boolean = {
        !it.className.contains("Loged") && !it.className.contains("Framing") &&
                it.className.contains(FILTER_BY_PACKAGE_NAME) && OTHER_CLASS_FILTER(it.className)
    }

    /**
     * If you want to set the [OTHER_CLASS_FILTER] up via Higher-Order Functions
     */
    @Suppress("FunctionName")
    fun OTHER_CLASS_FILTER(block: (String) -> Boolean) = run { OTHER_CLASS_FILTER = block }

    /**
     * If you want to add an interceptor to add the logs to a certain location, make a class that implements [LogedInterceptor]
     */
    fun logedInterceptor(block: (level: LogLevel, tag: String, msg: String) -> Unit) {
        logedInterceptor = object : LogedInterceptor {
            override fun log(level: LogLevel, tag: String, msg: String) = block(level, tag, msg)
        }
    }

    /**
     * The show pretty method
     */
    private fun prettyLog(tag: String, msg: Any?, level: Int, threadName: Boolean) {
        val wanted = Thread.currentThread().stackTrace.filter(filter).map(stackToString)
            .let { it.mapIndexed { index, s -> "\n${if (index == 0) "╚" else "\t${if (index + 1 < it.size) '╠' else '╚'}"}═▷\t$s" } }
            .joinToString("")
        print(tag, "$msg$wanted", level, threadName)
    }

    /**
     * The not show pretty method
     */
    private fun log(tag: String, msg: Any?, level: Int, threadName: Boolean) =
        print(tag, "${msg.toString()}\n╚═▷\t${Thread.currentThread().stackTrace.firstOrNull(filter)?.stackToString()}", level, threadName)

    /**
     * Actually printing to the console
     */
    private fun print(tag: String, msg: String, level: Int, threadName: Boolean) {
        if (UNIT_TESTING) println(msg) else Log.println(level, tag + if (threadName) "/${Thread.currentThread().name}" else "", msg)
    }

    /**
     * Delegating the showPretty or not
     */
    private fun delegate(tag: String, msg: Any?, level: Int, threadName: Boolean, showPretty: Boolean = SHOW_PRETTY) =
        logedInterceptor?.log(LogLevel(level), tag, msg.toString())
            .also { if (showPretty) prettyLog(tag, msg, level, threadName) else log(tag, msg, level, threadName) }
            .let { Unit }

    /**
     * Error log
     *
     * @param msg the message to log
     */
    @JvmStatic
    @JvmOverloads
    fun e(msg: Any? = null, tag: String = TAG, showPretty: Boolean = SHOW_PRETTY, threadName: Boolean = WITH_THREAD_NAME) =
        delegate(tag, msg, Log.ERROR, threadName, showPretty)

    /**
     * Info log
     *
     * @param msg the message to log
     */
    @JvmStatic
    @JvmOverloads
    fun i(msg: Any? = null, tag: String = TAG, showPretty: Boolean = SHOW_PRETTY, threadName: Boolean = WITH_THREAD_NAME) =
        delegate(tag, msg, Log.INFO, threadName, showPretty)

    /**
     * Assert log
     *
     * @param msg the message to log
     */
    @JvmStatic
    @JvmOverloads
    fun a(msg: Any? = null, tag: String = TAG, showPretty: Boolean = SHOW_PRETTY, threadName: Boolean = WITH_THREAD_NAME) =
        delegate(tag, msg, Log.ASSERT, threadName, showPretty)

    /**
     * What a Terrible Failure log
     *
     * @param msg the message to log
     */
    @JvmStatic
    @JvmOverloads
    fun wtf(msg: Any? = null, tag: String = TAG, showPretty: Boolean = SHOW_PRETTY, threadName: Boolean = WITH_THREAD_NAME) =
        delegate(tag, msg, Log.ASSERT, threadName, showPretty)

    /**
     * Warning log
     *
     * @param msg the message to log
     */
    @JvmStatic
    @JvmOverloads
    fun w(msg: Any? = null, tag: String = TAG, showPretty: Boolean = SHOW_PRETTY, threadName: Boolean = WITH_THREAD_NAME) =
        delegate(tag, msg, Log.WARN, threadName, showPretty)

    /**
     * Debug log
     *
     * @param msg the message to log
     */
    @JvmStatic
    @JvmOverloads
    fun d(msg: Any? = null, tag: String = TAG, showPretty: Boolean = SHOW_PRETTY, threadName: Boolean = WITH_THREAD_NAME) =
        delegate(tag, msg, Log.DEBUG, threadName, showPretty)

    /**
     * Verbose log
     *
     * @param msg the message to log
     */
    @JvmStatic
    @JvmOverloads
    fun v(msg: Any? = null, tag: String = TAG, showPretty: Boolean = SHOW_PRETTY, threadName: Boolean = WITH_THREAD_NAME) =
        delegate(tag, msg, Log.VERBOSE, threadName, showPretty)

    /**
     * Random log
     *
     * @param msg the message to log
     * @param choices the different priority levels. MUST be between 2-7
     *  ### [Log.VERBOSE] = 2
     *  ### [Log.DEBUG] = 3
     *  ### [Log.INFO] = 4
     *  ### [Log.WARN] = 5
     *  ### [Log.ERROR] = 6
     *  ### [Log.ASSERT] = 7
     */
    @JvmStatic
    @JvmOverloads
    fun r(
        msg: Any? = null, tag: String = TAG, showPretty: Boolean = SHOW_PRETTY, threadName: Boolean = WITH_THREAD_NAME,
        vararg choices: Int = intArrayOf(2, 3, 4, 5, 6, 7)
    ) = delegate(tag, msg, choices.random().let { if (it in 2..7) it else (2..7).random() }, threadName, showPretty)
}
