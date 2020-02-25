package com.programmersbox.helpfulutils

import android.graphics.Color
import android.os.Build
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
    @androidx.annotation.IntRange(from = 0, to = 255) alpha: Int = nextInt(0, 255),
    @androidx.annotation.IntRange(from = 0, to = 255) red: Int = nextInt(0, 255),
    @androidx.annotation.IntRange(from = 0, to = 255) green: Int = nextInt(0, 255),
    @androidx.annotation.IntRange(from = 0, to = 255) blue: Int = nextInt(0, 255)
): Int = Color.argb(alpha, red, green, blue)

data class DeviceInfo(val board: String = Build.BOARD,
                      val brand: String = Build.BRAND,
                      val device: String = Build.DEVICE,
                      val manufacturer: String = Build.MANUFACTURER,
                      val model: String = Build.MODEL,
                      val product: String = Build.PRODUCT,
                      val sdkInt: Int = Build.VERSION.SDK_INT,
                      val versionCode: String = Build.VERSION_CODES::class.java.fields[Build.VERSION.SDK_INT].name,
                      val versionNumber: String = Build.VERSION.RELEASE)