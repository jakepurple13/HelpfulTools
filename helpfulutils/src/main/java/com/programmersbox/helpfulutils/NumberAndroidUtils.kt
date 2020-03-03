package com.programmersbox.helpfulutils

import android.graphics.Color

fun Colors.ARGB.toColor() = Color.argb(a, r, g, b)
fun Colors.RGB.toColor() = Color.rgb(r, g, b)
fun Colors.CMYK.toColor() = toARGB().toColor()