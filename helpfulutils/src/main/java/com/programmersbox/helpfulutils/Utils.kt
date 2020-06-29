package com.programmersbox.helpfulutils

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.os.CountDownTimer
import androidx.annotation.RequiresApi
import java.time.Duration
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.math.min
import kotlin.random.Random

/**
 * what if given
 * this makes things easier to chain methods together
 */
inline fun <T> T.whatIf(
    given: Boolean?,
    whatIfTrue: T.() -> Unit,
    whatIfFalse: T.() -> Unit
) = apply { if (given == true) whatIfTrue() else whatIfFalse() }

/**
 * what if given
 * this makes things easier to chain methods together
 */
inline fun <T> T.whatIf(
    given: Boolean?,
    whatIfTrue: T.() -> Unit
) = apply { if (given == true) whatIfTrue() }

/**
 * what if [given] is not null
 */
inline fun <T, R> T.whatIfNotNull(
    given: R?,
    whatIfNotNull: T.(R) -> Unit
) = apply { given?.let { whatIfNotNull(it) } }

/**
 * @return a random color
 */
fun Random.nextColor(
    alpha: Int = nextInt(0, 255),
    red: Int = nextInt(0, 255),
    green: Int = nextInt(0, 255),
    blue: Int = nextInt(0, 255)
): Int = Color.argb(alpha, red, green, blue)

/**
 * @return a random string of [length]
 */
fun Random.nextString(length: Int = 1) = StringBuilder().apply { repeat(length) { append((nextInt(96) + 32).toChar()) } }.toString()

/**
 * This just gives Device Information and Runtime Information
 */
object DeviceInfo {
    data class Info(
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

    data class RuntimeInfo(
        val availableProcessors: Int = Runtime.getRuntime().availableProcessors(),
        val freeMemory: Long = Runtime.getRuntime().freeMemory(),
        val totalMemory: Long = Runtime.getRuntime().totalMemory(),
        val maxMemory: Long = Runtime.getRuntime().maxMemory()
    )
}

/**
 * An easy way to create a [CountDownTimer]
 *
 * e.g.
 *
 *    EasyCountDownTimer(1000) { println("OnFinished") }
 *    //or
 *    EasyCountDownTimer(1000, 1000, { println(it) }) { println("OnFinished") }
 */
object EasyCountDownTimer {
    @JvmStatic
    @JvmOverloads
    operator fun invoke(
        millisInFuture: Long, countdownInterval: Long = 1000,
        onTick: (millisUntilFinished: Long) -> Unit = {}, finished: () -> Unit
    ) = object : CountDownTimer(millisInFuture, countdownInterval) {
        override fun onTick(millisUntilFinished: Long) = onTick(millisUntilFinished)
        override fun onFinish(): Unit = finished()
    }
}

/**
 * Calculates the similarity (a number within 0.0 and 1.0) between two strings.
 */
fun String.similarity(s2: String): Double {
    var longer = this
    var shorter = s2
    if (this.length < s2.length) { // longer should always have greater length
        longer = s2
        shorter = this
    }
    val longerLength = longer.length
    return if (longerLength == 0) {
        1.0 /* both strings are zero length */
    } else (longerLength - editDistance(longer, shorter)) / longerLength.toDouble()
    /* // If you have Apache Commons Text, you can use it to calculate the edit distance:
        LevenshteinDistance levenshteinDistance = new LevenshteinDistance();
        return (longerLength - levenshteinDistance.apply(longer, shorter)) / (double) longerLength; */
}

// Example implementation of the Levenshtein Edit Distance
// See http://rosettacode.org/wiki/Levenshtein_distance#Java
@SuppressLint("DefaultLocale")
@Suppress("NAME_SHADOWING")
private fun editDistance(s1: String, s2: String): Int {
    var s1 = s1
    var s2 = s2
    s1 = s1.toLowerCase()
    s2 = s2.toLowerCase()
    val costs = IntArray(s2.length + 1)
    for (i in 0..s1.length) {
        var lastValue = i
        for (j in 0..s2.length) {
            if (i == 0) costs[j] = j else {
                if (j > 0) {
                    var newValue = costs[j - 1]
                    if (s1[i - 1] != s2[j - 1]) newValue = min(min(newValue, lastValue), costs[j]) + 1
                    costs[j - 1] = lastValue
                    lastValue = newValue
                }
            }
        }
        if (i > 0) costs[s2.length] = lastValue
    }
    return costs[s2.length]
}

@RequiresApi(Build.VERSION_CODES.O)
fun timeToNextHour(): Long {
    val start = ZonedDateTime.now()
    // Hour + 1, set Minute and Second to 00
    val end = start.plusHours(1).truncatedTo(ChronoUnit.HOURS)

    // Get Duration
    val duration = Duration.between(start, end)
    return duration.toMillis()
}

@RequiresApi(Build.VERSION_CODES.O)
fun timeToNextHourOrHalf(): Long {
    val start = ZonedDateTime.now()
    // Hour + 1, set Minute and Second to 00
    val hour = start.plusHours(1).truncatedTo(ChronoUnit.HOURS)
    val minute = start.plusHours(0).truncatedTo(ChronoUnit.HOURS)
        .plusMinutes(30).truncatedTo(ChronoUnit.MINUTES).plusSeconds(1)

    // Get Duration
    val durationHour = Duration.between(start, hour).toMillis()
    val durationMinute = Duration.between(start, minute).toMillis()
    return if (durationHour <= durationMinute) durationHour else durationMinute
}

fun timeToNext(minuteInMs: Long = 1_800_000) =
    Date(minuteInMs * ((System.currentTimeMillis() + (minuteInMs / 2)) / minuteInMs)).time - System.currentTimeMillis()

val Calendar.timeToNextHourOrHalf
    @RequiresApi(Build.VERSION_CODES.O)
    get() = timeToNextHourOrHalf()

val Calendar.timeToNextHour
    @RequiresApi(Build.VERSION_CODES.O)
    get() = timeToNextHour()

fun Date.isBetween(min: Date, max: Date) = after(min) && before(max)

fun Long.isDateBetween(min: Date, max: Date) = Date(this).isBetween(min, max)

fun Date.isBetween(min: Long, max: Long) = isBetween(Date(min), Date(max))

fun Long.isDateBetween(min: Long, max: Long) = Date(this).isBetween(min, max)