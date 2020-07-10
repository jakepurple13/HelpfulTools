package com.programmersbox.thirdpartyutils

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.core.graphics.drawable.toBitmap
import androidx.palette.graphics.Palette

fun Drawable.getPalette() = Palette.from(toBitmap()).generate()
fun Bitmap.getPalette() = Palette.from(this).generate()

/*fun Int.getPaletteFromColor() = Bitmap.createBitmap(5, 5, Bitmap.Config.ARGB_8888)
    .applyCanvas { drawColor(this@getPaletteFromColor) }
    .let { Palette.from(it).generate() }

fun String.getPaletteFromHexColor() = Bitmap.createBitmap(5, 5, Bitmap.Config.ARGB_8888)
    .applyCanvas { drawColor(Color.parseColor(this@getPaletteFromHexColor)) }
    .let { Palette.from(it).generate() }*/

fun Int.toColorDrawable() = ColorDrawable(this)
fun String.toColorDrawable() = ColorDrawable(Color.parseColor(this))