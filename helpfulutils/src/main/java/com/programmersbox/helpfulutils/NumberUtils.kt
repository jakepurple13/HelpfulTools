package com.programmersbox.helpfulutils

import java.util.*
import kotlin.random.Random

/**
 * Gives a time representation for this long.
 * 10:40 if no hours
 * or
 * 02:05:50 if there are hours
 */
fun <T : Number> T.stringForTime(): String? {
    var millisecond = this.toLong()
    if (millisecond < 0 || millisecond >= 24 * 60 * 60 * 1000) return "00:00"
    millisecond /= 1000
    var minute = (millisecond / 60).toInt()
    val hour = minute / 60
    val second = (millisecond % 60).toInt()
    minute %= 60
    val stringBuilder = StringBuilder()
    val mFormatter = Formatter(stringBuilder, Locale.getDefault())
    return if (hour > 0) {
        mFormatter.format("%02d:%02d:%02d", hour, minute, second)
    } else {
        mFormatter.format("%02d:%02d", minute, second)
    }.toString()
}

/**
 * @return a string of random characters of [this] length
 */
fun <T : Number> T.randomString(): String = StringBuilder().apply { repeat(toInt()) { append((Random.nextInt(96) + 32).toChar()) } }.toString()