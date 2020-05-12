package com.programmersbox.helpfulutils

import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.os.Build
import android.transition.AutoTransition
import android.transition.Transition
import android.transition.TransitionManager
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.LayoutRes
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet


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
@RequiresApi(Build.VERSION_CODES.KITKAT)
fun <T : ViewGroup> T.animateChildren(transition: Transition? = AutoTransition(), block: T.() -> Unit) =
    TransitionManager.beginDelayedTransition(this, transition).apply { block() }

/**
 * This will take care of [ConstraintSet.applyTo] for you
 */
@RequiresApi(Build.VERSION_CODES.KITKAT)
class ConstraintRange(private val original: ConstraintLayout, vararg items: ConstraintSet, loop: Boolean = true) :
    ItemRange<ConstraintSet>(*items, loop = loop) {
    override operator fun inc(): ConstraintRange {
        super.inc()
        original.animateChildren { item.applyTo(original) }
        return this
    }

    override operator fun dec(): ConstraintRange {
        super.dec()
        original.animateChildren { item.applyTo(original) }
        return this
    }
}

/**
 * Set the visibility to [View.GONE]
 */
fun View.gone() = run { visibility = View.GONE }

/**
 * Set the visibility to [View.INVISIBLE]
 */
fun View.invisible() = run { visibility = View.INVISIBLE }

/**
 * Set the visibility to [View.VISIBLE]
 */
fun View.visible() = run { visibility = View.VISIBLE }

/**
 * changes color of a drawable
 */
fun Drawable.changeDrawableColor(color: Int) {
    mutate().colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY)
}

/**
 * hides the keyboard
 */
fun View.hideKeyboard() {
    clearFocus()
    context.inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
}

/**
 * shows the keyboard
 */
fun View.showKeyboard() {
    requestFocus()
    context.inputMethodManager.showSoftInput(this, InputMethodManager.SHOW_FORCED)
}

/**
 * @see AlertDialog.Builder.setItems
 * This method works only with enum values, making it easy to get the wanted enum.
 * This will return the enum with the ordinal value of the selected index
 */
inline fun <reified T : Enum<T>> AlertDialog.Builder.setEnumItems(
    items: Array<out CharSequence>,
    crossinline action: (item: T, dialog: DialogInterface) -> Unit
): AlertDialog.Builder = setItems(items) { d, index ->
    val clazz = T::class.java
    if (clazz.isEnum) clazz.enumConstants?.get(index)?.let { action(it, d) }
}

/**
 * @see AlertDialog.Builder.setSingleChoiceItems
 * This method works only with enum values, making it easy to get the wanted enum.
 * This will return the enum with the ordinal value of the selected index
 */
inline fun <reified T : Enum<T>> AlertDialog.Builder.setEnumSingleChoiceItems(
    items: Array<out CharSequence>,
    checkedItem: T? = null,
    crossinline action: (item: T, dialog: DialogInterface) -> Unit
): AlertDialog.Builder = apply {
    val clazz = T::class.java
    if (clazz.isEnum) {
        val enums = clazz.enumConstants
        val itemIndex = checkedItem?.let { enums?.indexOf(it) } ?: -1
        setSingleChoiceItems(items, itemIndex) { d, index -> enums?.get(index)?.let { action(it, d) } }
    }
}

/**
 * @see AlertDialog.Builder.setMultiChoiceItems
 * This method works only with enum values, making it easy to get the wanted enum.
 * This will return the enum with the ordinal value of the selected index
 */
inline fun <reified T : Enum<T>> AlertDialog.Builder.setEnumMultiChoiceItems(
    items: Array<out CharSequence>,
    vararg checkedItems: T,
    crossinline action: (item: T, checked: Boolean, dialog: DialogInterface) -> Unit
): AlertDialog.Builder = apply {
    val clazz = T::class.java
    if (clazz.isEnum) {
        val enums = clazz.enumConstants
        val boolArray = BooleanArray(enums?.size ?: 1) { false }
        checkedItems.forEach { enums?.indexOf(it)?.let { if (it != -1) boolArray[it] = true } }
        setMultiChoiceItems(items, boolArray) { d, index, bool -> enums?.get(index)?.let { action(it, bool, d) } }
    }
}

/**
 * @see AlertDialog.Builder.setView
 * but allows setup for everything with the higher-order function
 */
fun AlertDialog.Builder.setView(@LayoutRes layoutRes: Int, viewSetup: View.() -> Unit) =
    setView(LayoutInflater.from(context).inflate(layoutRes, null, false).apply(viewSetup))

/**
 * @see AlertDialog.Builder.setCustomTitle
 * but allows setup for everything with the higher-order function
 */
fun AlertDialog.Builder.setCustomTitle(@LayoutRes layoutRes: Int, viewSetup: View.() -> Unit) =
    setCustomTitle(LayoutInflater.from(context).inflate(layoutRes, null, false).apply(viewSetup))
