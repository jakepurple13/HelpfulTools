package com.programmersbox.thirdpartyutils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.core.graphics.drawable.toBitmap
import androidx.palette.graphics.Palette

fun Drawable.getPalette() = Palette.from(toBitmap()).generate()
fun Bitmap.getPalette() = Palette.from(this).generate()

fun Int.getPaletteFromColor(): Palette {
    val b = Bitmap.createBitmap(5, 5, Bitmap.Config.ARGB_8888)
    Canvas(b).drawColor(this@getPaletteFromColor)
    return b.getPalette()
}

fun String.getPaletteFromHexColor(): Palette = Color.parseColor(this@getPaletteFromHexColor).getPaletteFromColor()

fun Int.toColorDrawable() = ColorDrawable(this)
fun String.toColorDrawable() = ColorDrawable(Color.parseColor(this))