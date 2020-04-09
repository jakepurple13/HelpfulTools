package com.programmersbox.helpfulutils

import android.graphics.Color
import android.os.Build
import android.os.CountDownTimer
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
 *    EasyCountDownTimer(1000, { println(it) }) { println("OnFinished") }
 */
object EasyCountDownTimer {
    operator fun invoke(millisInFuture: Long, onTick: (Long) -> Unit = {}, finished: () -> Unit) = object : CountDownTimer(millisInFuture, 1000) {
        override fun onTick(millisUntilFinished: Long) = onTick(millisUntilFinished)
        override fun onFinish(): Unit = finished()
    }
}
