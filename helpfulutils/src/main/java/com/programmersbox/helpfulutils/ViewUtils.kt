package com.programmersbox.helpfulutils

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.TextView

var TextView.startDrawable: Drawable?
    get() = compoundDrawables[0]
    set(value) = setCompoundDrawablesWithIntrinsicBounds(value, topDrawable, endDrawable, bottomDrawable)
var TextView.endDrawable: Drawable?
    get() = compoundDrawables[2]
    set(value) = setCompoundDrawablesWithIntrinsicBounds(startDrawable, topDrawable, value, bottomDrawable)
var TextView.topDrawable: Drawable?
    get() = compoundDrawables[1]
    set(value) = setCompoundDrawablesWithIntrinsicBounds(startDrawable, value, endDrawable, bottomDrawable)
var TextView.bottomDrawable: Drawable?
    get() = compoundDrawables[3]
    set(value) = setCompoundDrawablesWithIntrinsicBounds(startDrawable, topDrawable, endDrawable, value)

fun View.postDelayed(delayMillis: Long, block: () -> Unit) = postDelayed(block, delayMillis)