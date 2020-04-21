package com.programmersbox.funutils.views

import android.animation.Animator
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.annotation.UiThread
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.interpolator.view.animation.FastOutLinearInInterpolator
import org.xmlpull.v1.XmlPullParser


/**
 * Flashes the activity
 * @see flash
 */
@UiThread
fun <T : AppCompatActivity> T.flashScreen(
    color: Int = Color.WHITE,
    duration: Long = 500,
    doOnEnd: () -> Unit = {}
) {
    val params1 =
        ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    val rl = RelativeLayout(this@flashScreen)
    rl.setBackgroundColor(color)
    rl.z = Float.MAX_VALUE

    val view = findViewById<ViewGroup>(android.R.id.content).getChildAt(0) as ViewGroup

    view.addView(rl, params1)

    rl.alpha = 1f
    rl.animate()
        .alpha(0f)
        .setDuration(duration)
        .setInterpolator(FastOutLinearInInterpolator())
        .withEndAction {
            rl.alpha = 0f
            view.removeView(rl)
            doOnEnd()
        }
        .start()
}

/**
 * Flashes a ViewGroup
 * @see flash
 */
@UiThread
fun <T : ViewGroup> T.flashScreen(
    color: Int = Color.WHITE,
    duration: Long = 500,
    paramSetup: ((RelativeLayout.LayoutParams) -> Unit)? = null,
    doOnEnd: () -> Unit = {}
) {
    val default = paramSetup != null
    val params1 =
        RelativeLayout.LayoutParams(
            if (default) ViewGroup.LayoutParams.WRAP_CONTENT else ViewGroup.LayoutParams.MATCH_PARENT,
            if (default) ViewGroup.LayoutParams.WRAP_CONTENT else ViewGroup.LayoutParams.MATCH_PARENT
        )
    if (default) {
        paramSetup!!(params1)
    }
    val rl = RelativeLayout(context)
    rl.setBackgroundColor(color)
    rl.z = Float.MAX_VALUE

    addView(rl, params1)

    rl.animate()
        .alpha(0f)
        .setDuration(duration)
        .setInterpolator(FastOutLinearInInterpolator())
        .withEndAction {
            removeView(rl)
            doOnEnd()
        }
        .start()
}

/**
 * Flashes a ViewGroup
 * @see flash
 */
@UiThread
fun <T : ViewGroup> T.flashScreen(
    color: Int = Color.WHITE,
    duration: Long = 500,
    paramSetup: RelativeLayout.LayoutParams,
    doOnEnd: () -> Unit = {}
) {
    val rl = RelativeLayout(context)
    rl.setBackgroundColor(color)
    rl.z = Float.MAX_VALUE

    addView(rl, paramSetup)

    rl.alpha = 1f
    rl.animate()
        .alpha(0f)
        .setDuration(duration)
        .setInterpolator(FastOutLinearInInterpolator())
        .withEndAction {
            rl.alpha = 0f
            removeView(rl)
            doOnEnd()
        }
        .start()
}

private fun generateLayoutParamsSet(viewGroup: ViewGroup): ViewGroup.LayoutParams? {
    val parser = viewGroup.resources.getLayout(viewGroup.id)
    try {
        while (parser.nextToken() != XmlPullParser.START_TAG) {
            // Skip everything until the view tag.
        }
        return viewGroup.generateLayoutParams(parser)
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return null
}

@UiThread
fun <U : ViewGroup.LayoutParams> ViewGroup.flashScreens(
    color: Int = Color.WHITE,
    duration: Long = 500,
    paramSetup: (U.() -> Unit) = {},
    doOnEnd: () -> Unit = {}
) {
    @Suppress("UNCHECKED_CAST") val params1: U = (generateLayoutParamsSet(this@flashScreens) as U).apply(paramSetup)
    val rl = RelativeLayout(context)
    rl.setBackgroundColor(color)
    rl.z = Float.MAX_VALUE

    addView(rl, 0, params1)

    rl.alpha = 1f
    rl.animate()
        .alpha(0f)
        .setDuration(duration)
        .setInterpolator(FastOutLinearInInterpolator())
        .withEndAction {
            rl.alpha = 0f
            removeView(rl)
            doOnEnd()
        }
        .start()
}

/**
 * Flashes a view
 * @param prevColor the original color (Default is [T].solidColor)
 * @param color the color you want to flash (Default is [Color.WHITE])
 * @param duration (ms) how long do you want the flash to happen (Default is 500)
 * @param onUpdate ([Int], [T]) Int is the color, View is [T]. This is so you can choose how the view's color is changed (This must be included)
 * @param doOnEnd if you want something to happen once the flash is over (Not needed)
 * @param doOnStart if you want something to happen when the flash starts (Not needed)
 */
@UiThread
fun <T : View> T.flash(
    prevColor: Int = solidColor,
    color: Int = Color.WHITE,
    duration: Long = 500,
    doOnStart: (Animator) -> Unit = {},
    doOnEnd: (Animator, View) -> Unit = { _, _ -> },
    onUpdate: (Int, T) -> Unit
) {
    val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), color, prevColor)
    colorAnimation.duration = duration // milliseconds
    colorAnimation.addUpdateListener { animator -> onUpdate(animator.animatedValue as Int, this@flash) }
    colorAnimation.doOnStart(doOnStart)
    colorAnimation.doOnEnd { doOnEnd(it, this@flash) }
    colorAnimation.start()
}

var <T : View> T.backgroundColor: Int
    get() {
        return try {
            (background as ColorDrawable).color
        } catch (e: Exception) {
            solidColor
        }
    }
    set(value) {
        setBackgroundColor(value)
    }

object Flash {
    /**
     * @see ViewGroup.flashScreen
     */
    fun layoutFlash(
        view: ViewGroup,
        color: Int = Color.WHITE,
        duration: Long = 500,
        doOnStart: (Animator) -> Unit = {},
        doOnEnd: (Animator, View) -> Unit = { _, _ -> }
    ) = view.flash(view.backgroundColor, color, duration, doOnEnd = doOnEnd, doOnStart = doOnStart) { colors, views ->
        views.setBackgroundColor(colors)
    }

    fun flashViews(
        prevColor: Int = Color.BLACK,
        color: Int = Color.WHITE,
        duration: Long = 500,
        doOnStart: (Animator) -> Unit = {},
        doOnEnd: (Animator) -> Unit = {},
        onUpdate: (Int) -> Unit
    ) {
        val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), color, prevColor)
        colorAnimation.duration = duration // milliseconds
        colorAnimation.addUpdateListener { animator ->
            onUpdate(animator.animatedValue as Int)
        }
        colorAnimation.doOnStart(doOnStart)
        colorAnimation.doOnEnd(doOnEnd)
        colorAnimation.start()
    }

    fun flashAllViewsInGroup(
        view: ViewGroup,
        color: Int = Color.WHITE,
        duration: Long = 500,
        doOnStart: (Animator) -> Unit = {},
        doOnEnd: (Animator, View) -> Unit = { _, _ -> },
        doOnFinalEnd: () -> Unit = {},
        onUpdate: (Int, View) -> Unit
    ) {
        view.flash(view.backgroundColor, color, duration, doOnStart, doOnEnd, onUpdate)
        for (i in 0..view.childCount) {
            val v = view.getChildAt(i)
            v?.flash(v.backgroundColor, color, duration, doOnStart, doOnEnd, onUpdate)
        }
        doOnFinalEnd()
    }

}