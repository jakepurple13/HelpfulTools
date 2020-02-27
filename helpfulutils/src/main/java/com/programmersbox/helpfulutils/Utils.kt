package com.programmersbox.helpfulutils

import android.graphics.Color
import android.os.Build
import java.util.*
import kotlin.random.Random

inline fun <T> T.whatIf(
    given: Boolean?,
    whatIfTrue: T.() -> Unit,
    whatIfFalse: T.() -> Unit
) = apply { if (given == true) whatIfTrue() else whatIfFalse() }

inline fun <T> T.whatIf(
    given: Boolean?,
    whatIfTrue: T.() -> Unit
) = apply { if (given == true) whatIfTrue() }

inline fun <T, R> T.whatIfNotNull(
    given: R?,
    whatIfNotNull: T.(R) -> Unit
) = apply { given?.let { whatIfNotNull(it) } }

fun <T> MutableList<T>.addAll(vararg args: T) = addAll(args)

fun Random.nextColor(
    alpha: Int = nextInt(0, 255),
    red: Int = nextInt(0, 255),
    green: Int = nextInt(0, 255),
    blue: Int = nextInt(0, 255)
): Int = Color.argb(alpha, red, green, blue)

data class DeviceInfo(
    val board: String = Build.BOARD,
    val brand: String = Build.BRAND,
    val device: String = Build.DEVICE,
    val manufacturer: String = Build.MANUFACTURER,
    val model: String = Build.MODEL,
    val product: String = Build.PRODUCT,
    val sdkInt: Int = Build.VERSION.SDK_INT,
    val versionCode: String = Build.VERSION_CODES::class.java.fields[Build.VERSION.SDK_INT].name,
    val versionNumber: String = Build.VERSION.RELEASE
)

/**
 * Gives a time representation for this long.
 * 10:40 if no hours
 * or
 * 02:05:50 if there are hours
 */
fun Long.stringForTime(): String? {
    var millisecond = this
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
 * @see [Long.stringForTime]
 */
fun Int.stringForTime() = toLong().stringForTime()