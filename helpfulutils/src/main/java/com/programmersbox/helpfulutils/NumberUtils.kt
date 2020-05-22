package com.programmersbox.helpfulutils

import java.util.*
import kotlin.math.min
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

    data class ARGB(val a: Int, val r: Int, val g: Int, val b: Int) : Colors() {
        fun toRGB(): RGB = RGB(r, g, b)
        fun toCMYK(): CMYK = toRGB().toCMYK()
        fun toInt(): Int {
            var col: Int = 0xff or 24 shl a
            col = 0xff or 16 shl r and col
            col = 0xff or 8 shl g and col
            col = 0xff or b and col
            return col
        }
    }

    data class RGB(val r: Int, val g: Int, val b: Int) : Colors() {
        fun toCMYK(): CMYK {
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

        fun toARGB() = ARGB(255, r, g, b)

        fun toInt(): Int {
            var col: Int = 0xff or 16 shl r
            col = 0xff or 8 shl g and col
            col = 0xff or b and col
            return col
        }
    }

    data class CMYK(val c: Double, val m: Double, val y: Double, val k: Double) : Colors() {
        fun toRGB(): RGB {
            val c = this.c / 100.0
            val m = this.m / 100.0
            val y = this.y / 100.0
            val k = this.k / 100.0
            val r = 255 * (1 - c) * (1 - k)
            val g = 255 * (1 - m) * (1 - k)
            val b = 255 * (1 - y) * (1 - k)
            return RGB(r.roundToInt(), g.roundToInt(), b.roundToInt())
        }

        fun toARGB(): ARGB = toRGB().toARGB()
        fun toInt(): Int = toRGB().toInt()
    }
}


/**
 * Converts [this] to argb values
 */
fun Int.toARGB(): Colors.ARGB {
    val a = (this shr 24 and 0xff)// / 255.0f
    val r = (this shr 16 and 0xff)// / 255.0f
    val g = (this shr 8 and 0xff)// / 255.0f
    val b = (this and 0xff)// / 255.0f
    return Colors.ARGB(a, r, g, b)
}

/**
 * Converts [this] to rgb values
 */
fun Int.toRGB(): Colors.RGB {
    val r = (this shr 16 and 0xff)// / 255.0f
    val g = (this shr 8 and 0xff)// / 255.0f
    val b = (this and 0xff)// / 255.0f
    return Colors.RGB(r, g, b)
}

/**
 * Converts [this] to cmyk values
 */
fun Int.toCMYK(): Colors.CMYK {
    val r = (this shr 16 and 0xff)// / 255.0f
    val g = (this shr 8 and 0xff)// / 255.0f
    val b = (this and 0xff)// / 255.0f
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