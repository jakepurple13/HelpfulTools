package com.programmersbox.thirdpartyutils

import android.animation.ValueAnimator
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import androidx.annotation.ColorInt
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.model.KeyPath

var LottieAnimationView.checked: Boolean
    get() = progress == 1f
    set(value) = check(value)

fun LottieAnimationView.check(checked: Boolean) {
    val endProgress = if (checked) 1f else 0f
    val animator = ValueAnimator.ofFloat(progress, endProgress).apply {
        addUpdateListener { animation: ValueAnimator -> progress = animation.animatedValue as Float }
    }
    animator.start()
}

fun LottieAnimationView.changeTint(@ColorInt newColor: Int) =
    addValueCallback(KeyPath("**"), LottieProperty.COLOR_FILTER) { PorterDuffColorFilter(newColor, PorterDuff.Mode.SRC_ATOP) }
