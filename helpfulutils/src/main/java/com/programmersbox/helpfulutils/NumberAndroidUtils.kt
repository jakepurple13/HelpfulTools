package com.programmersbox.helpfulutils

import android.graphics.Color
import android.os.Handler
import androidx.core.os.postDelayed

fun Colors.ARGB.toColor() = Color.argb(a, r, g, b)
fun Colors.RGB.toColor() = Color.rgb(r, g, b)
fun Colors.CMYK.toColor() = toARGB().toColor()

fun Number.wait(block: () -> Unit) = Handler().postDelayed(this.toLong(), action = block)
