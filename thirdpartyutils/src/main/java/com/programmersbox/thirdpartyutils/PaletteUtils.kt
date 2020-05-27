package com.programmersbox.thirdpartyutils

import android.graphics.drawable.Drawable
import androidx.core.graphics.drawable.toBitmap
import androidx.palette.graphics.Palette

fun Drawable.getPalette() = Palette.from(toBitmap()).generate()