package com.programmersbox.helpfulutils

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.os.CountDownTimer
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
@JvmOverloads
fun Random.nextColor(
    alpha: Int = nextInt(0, 255),
    red: Int = nextInt(0, 255),
    green: Int = nextInt(0, 255),
    blue: Int = nextInt(0, 255)
): Int = Color.argb(alpha, red, green, blue)

/**
 * @return a random color
 */
@JvmOverloads
fun Random.nextRangeColor(
    alpha: IntRange = 0..255,
    red: IntRange = 0..255,
    green: IntRange = 0..255,
    blue: IntRange = 0..255
): Int = Color.argb(alpha.random(this), red.random(this), green.random(this), blue.random(this))

/**
 * @return a random string of [length]
 */
@JvmOverloads
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

/**
 * An easy way to create a Singleton Object
 * Try to only extend Objects with this. It won't really do anything with normal classes
 */
abstract class SingletonObject<T> {
    @Volatile
    private var instance: T? = null

    /**
     * Get instance of [T]
     */
    fun getInstance(vararg kv: Pair<String, Any>): T = instance ?: synchronized(this) { instance ?: create(kv.toMap()).also { instance = it } }

    protected abstract fun create(kv: Map<String, Any>): T
}

operator fun StringBuilder.plusAssign(s: Any?) {
    append(s)
}