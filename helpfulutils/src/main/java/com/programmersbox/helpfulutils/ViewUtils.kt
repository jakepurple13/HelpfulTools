package com.programmersbox.helpfulutils

import android.animation.ValueAnimator
import android.content.Context
import android.content.DialogInterface
import android.graphics.*
import android.graphics.drawable.Drawable
import android.os.Build
import android.transition.AutoTransition
import android.transition.Transition
import android.transition.TransitionManager
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.LayoutRes
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.transition.doOnEnd

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
@JvmOverloads
@ColorInt
fun Context.colorFromTheme(@AttrRes colorAttr: Int, @ColorInt defaultColor: Int = Color.BLACK): Int = TypedValue().run typedValue@{
    this@colorFromTheme.theme.resolveAttribute(colorAttr, this@typedValue, true).run { if (this) data else defaultColor }
}

/**
 * Starts a transition manager
 */
@RequiresApi(Build.VERSION_CODES.KITKAT)
@JvmOverloads
fun <T : ViewGroup> T.animateChildren(transition: Transition? = AutoTransition(), block: T.() -> Unit) =
    TransitionManager.beginDelayedTransition(this, transition).apply { block() }

/**
 * This will take care of [ConstraintSet.applyTo] for you
 */
@RequiresApi(Build.VERSION_CODES.KITKAT)
class ConstraintRange(
    private val layout: ConstraintLayout,
    override val itemList: List<ConstraintSet>
) : Range<ConstraintSet>(), List<ConstraintSet> by itemList {
    constructor(layout: ConstraintLayout, vararg items: ConstraintSet) : this(layout, items.toList())

    override fun onChange(current: Int, item: ConstraintSet) = layout.animateChildren { item.applyTo(this) }
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

fun IntProgression.toValueAnimator() = ValueAnimator.ofInt(first, last)

fun ClosedFloatingPointRange<Float>.toValueAnimator() = ValueAnimator.ofFloat(start, endInclusive)

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
fun AlertDialog.Builder.setView(@LayoutRes layoutRes: Int, viewSetup: View.() -> Unit): AlertDialog.Builder =
    setView(LayoutInflater.from(context).inflate(layoutRes, null, false).apply(viewSetup))

/**
 * @see AlertDialog.Builder.setCustomTitle
 * but allows setup for everything with the higher-order function
 */
fun AlertDialog.Builder.setCustomTitle(@LayoutRes layoutRes: Int, viewSetup: View.() -> Unit): AlertDialog.Builder =
    setCustomTitle(LayoutInflater.from(context).inflate(layoutRes, null, false).apply(viewSetup))

/**
 * Taken from [Gist](https://gist.github.com/DDihanov/6624925ced3b4db6f4ce6cbe1704a891)
 *
1. Convenience method to animate several constraint layouts one after another
important to only pass in the constraint layout LAYOUT FILE ids
if you have a case where the inflated view from the fragment/activity is not a ConstraintLayout, then for the next
layouts which are going to act as frames, leave the parent out and only have the ConstraintLayout as root
this advice was taken from [Medium](https://medium.com/@harivigneshjayapalan/well-using-constraintset-within-the-scrollview-is-not-encouraged-position-and-behaviour-may-ac6a2c6facc3)
2. This file requires kotlin android extensions plugin

 *   example usage in a Fragment:
 *   where ConstraintLayout is the ConstraintLayout you want to animate
 * ```kotlin
 **** ConstraintLayout.chainAnimate(
 ****   R.layout.fragment_state_a,
 ****   R.layout.fragment_state_b,
 ****   R.layout.fragment_state_c
 **** )
 * ```
 */
@RequiresApi(Build.VERSION_CODES.KITKAT)
fun ConstraintLayout.chainAnimate(@LayoutRes vararg layoutIds: Int) {
    val iterator = layoutIds.toMutableList().iterator()

    val start = iterator.next()

    val transition = if (iterator.hasNext()) {
        iterator.remove()
        val transition = AutoTransition()
        transition.doOnEnd { chainAnimate(*layoutIds) }
        transition
    } else null

    constructSetAndAnimate(context, start, this, transition)
}

@RequiresApi(Build.VERSION_CODES.KITKAT)
private fun constructSetAndAnimate(context: Context, layoutId: Int, root: ConstraintLayout, transition: AutoTransition? = null) {
    val set = ConstraintSet().apply { clone(context, layoutId) }
    TransitionManager.beginDelayedTransition(root, transition)
    set.applyTo(root)
}

/**
 * Get/Set the text of the [EditText] without needing to call [EditText.getText].toString()
 */
var EditText.currentText: CharSequence
    get() = text
    set(value) = setText(value)

/**
 * Saves the view to a bitmap
 */
fun View.asBitmap(): Bitmap = Bitmap.createBitmap(layoutParams.width, layoutParams.height, Bitmap.Config.ARGB_8888)
    .also { bitmap -> draw(Canvas(bitmap)) }

/**
 * Check if [this] is in [view]
 */
infix fun PointF.isIn(view: View): Boolean {
    val rect = Rect(view.left, view.top, view.right, view.bottom)
    return rect.contains((view.left + x).toInt(), (view.top + y).toInt())
}

/**
 * Check if [this] is in [view]
 */
operator fun PointF.contains(view: View) = isIn(view)

/**
 * Check if [this] is in [view]
 */
infix fun MotionEvent.isIn(view: View): Boolean {
    val rect = Rect(view.left, view.top, view.right, view.bottom)
    return rect.contains((view.left + x).toInt(), (view.top + y).toInt())
}

/**
 * Check if [this] is in [view]
 */
operator fun MotionEvent.contains(view: View) = isIn(view)