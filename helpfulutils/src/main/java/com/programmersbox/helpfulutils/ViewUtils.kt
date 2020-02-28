package com.programmersbox.helpfulutils

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.transition.AutoTransition
import android.transition.Transition
import android.transition.TransitionManager
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt

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

/**
 * Default [View.postDelayed] method but with the two parameters swapped
 */
fun View.postDelayed(delayMillis: Long, block: () -> Unit) = postDelayed(block, delayMillis)

/**
 * get a color from the theme
 */
@ColorInt
fun Context.colorFromTheme(@AttrRes colorAttr: Int, @ColorInt defaultColor: Int = Color.BLACK): Int = TypedValue().run typedValue@{
    this@colorFromTheme.theme.resolveAttribute(colorAttr, this@typedValue, true).run { if (this) data else defaultColor }
}

/**
 * Starts a transition manager
 */
fun <T : ViewGroup> T.animateChildren(transition: Transition? = AutoTransition(), block: T.() -> Unit) =
    TransitionManager.beginDelayedTransition(this, transition).apply { block() }