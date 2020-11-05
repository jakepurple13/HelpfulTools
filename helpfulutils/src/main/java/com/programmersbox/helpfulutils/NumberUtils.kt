package com.programmersbox.helpfulutils

import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import java.util.*
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.round
import kotlin.math.roundToInt
import kotlin.random.Random

/**
 * Converts [this] to a hex string
 */
fun Int.toHexString() = "#${Integer.toHexString(this)}"

/**
 * Get's the rgb of an integer
 */
fun Int.valueOf(): Triple<Int, Int, Int> {
    val r = (this shr 16 and 0xff)// / 255.0f
    val g = (this shr 8 and 0xff)// / 255.0f
    val b = (this and 0xff)// / 255.0f
    return Triple(r, g, b)
}

internal const val RGB_MAX = 255

sealed class Colors {

    abstract fun toRGB(): RGB
    abstract fun toCMYK(): CMYK
    abstract fun toARGB(): ARGB
    abstract fun toInt(): Int

    data class ARGB(val a: Int, val r: Int, val g: Int, val b: Int) : Colors() {
        override fun toRGB(): RGB = RGB(r, g, b)
        override fun toCMYK(): CMYK = toRGB().toCMYK()
        override fun toARGB(): ARGB = this
        override fun toInt(): Int {
            /*var col: Int = 0xff or 24 shl a
            col = 0xff or 16 shl r and col
            col = 0xff or 8 shl g and col
            col = 0xff or b and col
            return col*/
            var col: Int = 24 shl a or 0xff
            col = 16 shl r or 0xff and col
            col = 8 shl g or 0xff and col
            col = b or 0xff and col
            return col
        }
    }

    data class RGB(val r: Int, val g: Int, val b: Int) : Colors() {
        override fun toCMYK(): CMYK {
            var computedC = 1 - (r.toDouble() / RGB_MAX)
            var computedM = 1 - (g.toDouble() / RGB_MAX)
            var computedY = 1 - (b.toDouble() / RGB_MAX)
            val minCMY = minOf(computedC, computedM, computedY)
            computedC = (computedC - minCMY) / (1 - minCMY)
            computedM = (computedM - minCMY) / (1 - minCMY)
            computedY = (computedY - minCMY) / (1 - minCMY)
            var computedK = minCMY
            computedC = String.format("%.3f", computedC).toDouble()
            computedM = String.format("%.3f", computedM).toDouble()
            computedY = String.format("%.3f", computedY).toDouble()
            computedK = String.format("%.3f", computedK).toDouble()
            return CMYK(computedC, computedM, computedY, computedK)
        }

        override fun toRGB(): RGB = this
        override fun toARGB() = ARGB(255, r, g, b)
        override fun toInt(): Int {
            var col: Int = 0xff or 16 shl r
            col = 0xff or 8 shl g and col
            col = 0xff or b and col
            return col
        }
    }

    data class CMYK(val c: Double, val m: Double, val y: Double, val k: Double) : Colors() {
        override fun toRGB(): RGB {
            val c = this.c / 100.0
            val m = this.m / 100.0
            val y = this.y / 100.0
            val k = this.k / 100.0
            val r = 255 * (1 - c) * (1 - k)
            val g = 255 * (1 - m) * (1 - k)
            val b = 255 * (1 - y) * (1 - k)
            return RGB(r.roundToInt(), g.roundToInt(), b.roundToInt())
        }

        override fun toCMYK(): CMYK = this
        override fun toARGB(): ARGB = toRGB().toARGB()
        override fun toInt(): Int = toRGB().toInt()
    }
}


/**
 * Converts [this] to argb values
 */
fun Int.toARGB(): Colors.ARGB {
    val a = (this shr 24 and 0xff)// / 255.0f
    val (r, g, b) = valueOf()
    return Colors.ARGB(a, r, g, b)
}

/**
 * Converts [this] to rgb values
 */
fun Int.toRGB(): Colors.RGB {
    val (r, g, b) = valueOf()
    return Colors.RGB(r, g, b)
}

/**
 * Converts [this] to cmyk values
 */
fun Int.toCMYK(): Colors.CMYK {
    val (r, g, b) = valueOf()
    var computedC = 1 - (r.toDouble() / RGB_MAX)
    var computedM = 1 - (g.toDouble() / RGB_MAX)
    var computedY = 1 - (b.toDouble() / RGB_MAX)
    val minCMY = min(computedC, min(computedM, computedY))
    computedC = (computedC - minCMY) / (1 - minCMY)
    computedM = (computedM - minCMY) / (1 - minCMY)
    computedY = (computedY - minCMY) / (1 - minCMY)
    var computedK = minCMY
    computedC = String.format("%.3f", computedC).toDouble()
    computedM = String.format("%.3f", computedM).toDouble()
    computedY = String.format("%.3f", computedY).toDouble()
    computedK = String.format("%.3f", computedK).toDouble()
    return Colors.CMYK(computedC, computedM, computedY, computedK)
}

/**
 * Gives a time representation for this long.
 * 10:40 if no hours
 * or
 * 02:05:50 if there are hours
 */
fun <T : Number> T.stringForTime(): String {
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

/**
 * A way to round a number to [decimals] places
 */
fun Number.round(decimals: Int): Double {
    var multiplier = 1.0
    repeat(decimals) { multiplier *= 10 }
    return round(toDouble() * multiplier) / multiplier
}

class StepGradient(private var color1: Int, private var color2: Int) {

    private var mSteps: Int = 0

    infix fun gradient(f: IntProgression): IntArray {
        mSteps = f.last
        return f.map { it.colorStep() }.toIntArray()
    }

    private fun Int.colorStep(): Int {
        val c1 = color1.valueOf()
        val c2 = color2.valueOf()
        return Color.rgb(
            (c1.first * (mSteps - this) + c2.first * this) / mSteps,
            (c1.second * (mSteps - this) + c2.second * this) / mSteps,
            (c1.third * (mSteps - this) + c2.third * this) / mSteps
        )
    }

    companion object {
        /**
         * Allows for multiple gradients in an array
         */
        fun multipleGradients(steps: IntProgression, vararg colors: Int) = colors
            .drop(1)
            .fold(intArrayOf(colors.first())) { a, c -> intArrayOf(*a, *(a.last() toColor c gradient steps)) }
            .distinct()
            .toIntArray()

        /**
         * Allows for multiple gradients in an array
         */
        fun multipleGradients(steps: IntProgression, colors: List<Int>) = colors
            .drop(1)
            .fold(intArrayOf(colors.first())) { a, c -> intArrayOf(*a, *(a.last() toColor c gradient steps)) }
            .distinct()
            .toIntArray()
    }
}

/**
 * Get gradient steps
 *
 * e.g.
 *
 *    val colors = 0xff0000 toColor 0x0000ff gradient 0..5
 *
 * will give five steps to get from the start to the end
 */
@RequiresApi(Build.VERSION_CODES.O)
infix fun Color.toColor(color: Color) = StepGradient(color1 = this.toArgb(), color2 = color.toArgb())

/**
 * Get gradient steps
 *
 * e.g.
 *
 *    val colors = 0xff0000 toColor 0x0000ff gradient 0..5
 *
 * will give five steps to get from the start to the end
 */
infix fun Int.toColor(color: Int) = StepGradient(color1 = this, color2 = color)

/**
 * Find the closest number to [this]
 */
fun Int.closestTo(vararg num: Int) = num.minByOrNull { abs(this - it) }!!

/**
 * Find the closest number to [this]
 */
fun Double.closestTo(vararg num: Double) = num.minByOrNull { abs(this - it) }!!

/**
 * Find the closest number to [this]
 */
fun Long.closestTo(vararg num: Long) = num.minByOrNull { abs(this - it) }!!

/**
 * Find the closest number to [this]
 */
fun Float.closestTo(vararg num: Float) = num.minByOrNull { abs(this - it) }!!
