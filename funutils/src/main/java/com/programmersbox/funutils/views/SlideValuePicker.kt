package com.programmersbox.funutils.views

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.CallSuper
import androidx.annotation.ColorInt
import androidx.core.content.res.getDimensionOrThrow
import androidx.core.content.res.getDimensionPixelSizeOrThrow
import androidx.core.content.withStyledAttributes
import androidx.core.math.MathUtils.clamp
import com.google.android.material.animation.AnimationUtils
import com.google.android.material.animation.ArgbEvaluatorCompat
import com.google.android.material.math.MathUtils.lerp
import com.programmersbox.funutils.R
import com.programmersbox.funutils.views.SlideValuePicker.Listener
import kotlin.properties.ObservableProperty
import kotlin.reflect.KProperty

/**
 * Thank you [Medium](https://medium.com/@shkcodes/building-slide-color-picker-f68bbe37543) for a good article for an awesome view
 */
open class SlideValuePicker @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr), View.OnTouchListener {

    companion object {
        const val GRADIENT_RECT_DEFAULT_HEIGHT_MULTIPLIER = 4.8F
    }

    private var radius: Float by viewProperty(0f)
    private var halfRectHeight: Float by viewProperty(0f)
    private var centerCircleY: Float by viewProperty(0F)
    private var textSize: Float by viewProperty(40F)
    private var startColor: Int = 0
    private var endColor: Int = 0
    private lateinit var gradient: LinearGradient
    private var originalRadius: Float = 0F
    private var heightMultiplier = GRADIENT_RECT_DEFAULT_HEIGHT_MULTIPLIER
    var progress = 0.5F
        private set
    private var listener: Listener? = null

    private val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
    }
    private val rectanglePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val centerX: Float get() = (width / 2).toFloat()
    private val centerY: Float get() = (height / 2).toFloat()
    private val expandedHeight: Float get() = originalRadius * heightMultiplier
    private val scaledDownCircleRadius: Float get() = originalRadius * 0.8F
    private val upperBound: Float get() = centerY - expandedHeight + radius
    private val lowerBound: Float get() = centerY + expandedHeight - radius

    private val animator: ValueAnimator by lazy {
        ValueAnimator.ofFloat().apply {
            setFloatValues(0F, 1F)
            interpolator = AnimationUtils.FAST_OUT_SLOW_IN_INTERPOLATOR
        }
    }

    init {
        context.withStyledAttributes(attrs, R.styleable.SlideValuePicker) {
            radius = getDimensionOrThrow(R.styleable.SlideValuePicker_radius)
            textSize = getDimensionPixelSizeOrThrow(R.styleable.SlideValuePicker_text_size).toFloat()
            startColor = getColor(R.styleable.SlideValuePicker_start_color, context.getColor(android.R.color.white))
            endColor = getColor(R.styleable.SlideValuePicker_end_color, context.getColor(android.R.color.black))
            heightMultiplier = getFloat(
                R.styleable.SlideValuePicker_expanded_height_multiplier,
                GRADIENT_RECT_DEFAULT_HEIGHT_MULTIPLIER
            )
            circlePaint.color = getColor(R.styleable.SlideValuePicker_circle_color, Color.WHITE)
            circlePaint.strokeWidth = getDimension(R.styleable.SlideValuePicker_circle_width, 4F)
        }
        originalRadius = radius
        halfRectHeight = originalRadius
        setColors()
        @Suppress("LeakingThis")
        setOnTouchListener(this)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        centerCircleY = centerY
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val size = originalRadius * 2
        setMeasuredDimension(size.toInt(), size.toInt())
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawRoundRect(
            centerX - originalRadius,
            centerY - halfRectHeight,
            centerX + originalRadius,
            centerY + halfRectHeight,
            originalRadius,
            originalRadius,
            rectanglePaint
        )
        canvas.drawCircle(centerX, centerCircleY, radius, circlePaint)
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                runAnimation(TouchAction.DOWN)
                rectanglePaint.shader = gradient
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                runAnimation(TouchAction.UP)
                setSelectedColor(clamp(event.y, upperBound, lowerBound))
            }
            MotionEvent.ACTION_MOVE -> {
                centerCircleY = clamp(event.y, upperBound, lowerBound)
                progressChanged((centerCircleY - upperBound) / (lowerBound - upperBound))
            }
        }
        return true
    }

    private fun runAnimation(action: TouchAction) {
        animator.apply {
            removeAllUpdateListeners()
            cancel()
            addUpdateListener {
                val value = it.animatedValue as Float
                animateCircleScale(value, action)
                animateRectangle(value, action)
                if (action == TouchAction.UP) {
                    animateCircleToCenter(centerCircleY, value)
                }
            }
            start()
        }
    }

    private fun animateCircleScale(animatorValue: Float, action: TouchAction) {
        val (startRadius, endRadius) = if (action == TouchAction.UP) {
            scaledDownCircleRadius to originalRadius
        } else {
            originalRadius to scaledDownCircleRadius
        }
        radius = lerp(startRadius, endRadius, animatorValue)
    }

    private fun animateRectangle(animatorValue: Float, action: TouchAction) {
        val (startHeight, endHeight) = if (action == TouchAction.UP) {
            expandedHeight to originalRadius
        } else {
            originalRadius to expandedHeight
        }
        halfRectHeight = lerp(startHeight, endHeight, animatorValue)
    }

    private fun animateCircleToCenter(currentY: Float, animatorValue: Float) {
        centerCircleY = lerp(currentY, centerY, animatorValue)
    }

    private fun setSelectedColor(position: Float) {
        progress = (position - upperBound) / (lowerBound - upperBound)
        //listener?.onProgressChanged(progress)
        val colorRes = ArgbEvaluatorCompat.getInstance().evaluate(progress, startColor, endColor)
        rectanglePaint.shader = null
        rectanglePaint.color = colorRes
    }

    enum class TouchAction {
        DOWN, UP
    }

    private fun <T> viewProperty(default: T) = object : ObservableProperty<T>(default) {

        override fun beforeChange(property: KProperty<*>, oldValue: T, newValue: T): Boolean = newValue != oldValue

        override fun afterChange(property: KProperty<*>, oldValue: T, newValue: T) {
            postInvalidateOnAnimation()
        }
    }

    @CallSuper
    protected open fun progressChanged(progress: Float) {
        listener?.onProgressChanged(progress)
    }

    fun interface Listener {
        fun onProgressChanged(progress: Float)
    }

    fun setOnProgressChangeListener(listener: Listener?) = run { this.listener = listener }
    fun setOnProgressChangeListener(listener: (progress: Float) -> Unit) {
        this.listener = Listener { progress -> listener(progress) }
    }

    fun setStartColor(@ColorInt color: Int) {
        startColor = color
        setColors()
        postInvalidate()
    }

    fun setEndColor(@ColorInt color: Int) {
        endColor = color
        setColors()
        postInvalidate()
    }

    fun setCircleColor(@ColorInt color: Int) {
        circlePaint.color = color
        postInvalidate()
    }

    private fun setColors() {
        gradient = LinearGradient(
            0F,
            originalRadius - expandedHeight,
            0F,
            originalRadius + expandedHeight,
            startColor,
            endColor,
            Shader.TileMode.MIRROR
        )
        rectanglePaint.color = ArgbEvaluatorCompat.getInstance().evaluate(progress, startColor, endColor)
    }

}