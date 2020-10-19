package com.programmersbox.funutils.views

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.programmersbox.funutils.R
import kotlin.math.hypot


class DiamondLoader : View {
    constructor(context: Context) : super(context) {
        init(null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(attrs)
    }

    private fun <T> getOrThrow(block: () -> T) = try {
        block()
    } catch (e: Exception) {
        null
    }

    private fun init(attrs: AttributeSet?) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.DiamondLoader)
        progress = a.getInt(R.styleable.DiamondLoader_progress, if (isInEditMode) 75 else 0)
        loadingWidth = a.getDimension(R.styleable.DiamondLoader_lineWidth, 5f)
        progressColor = a.getColor(R.styleable.DiamondLoader_progressColor, Color.BLUE)
        emptyColor = a.getColor(R.styleable.DiamondLoader_emptyColor, Color.DKGRAY)
        bitmap = a.getDrawable(R.styleable.DiamondLoader_src)?.toBitmap()
        a.recycle()
        //if (isInEditMode) addCheckBoxHeader(0, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
    }

    fun setImageResource(@DrawableRes id: Int) {
        bitmap = ContextCompat.getDrawable(context, id)?.toBitmap()
    }

    fun setImageDrawable(drawable: Drawable?) {
        bitmap = drawable?.toBitmap()
    }

    fun setImageBitmap(bitmap: Bitmap?) {
        this.bitmap = bitmap
    }

    private var bitmap: Bitmap? = null
        set(value) {
            field = value
            postInvalidate()
        }

    private var loadingWidth: Float = 5f

    var progress: Int = if (isInEditMode) 75 else 0
        set(value) {
            field = if (value > 100) 100 else if (value < 0) 0 else value
            postInvalidate()
        }

    var emptyColor = Color.WHITE
        set(value) {
            field = value
            emptyPaint.color = value
            postInvalidate()
        }

    private val emptyPaint by lazy { newStrokePaint { color = emptyColor } }

    var progressColor = Color.BLUE
        set(value) {
            field = value
            progressPaint.color = value
            postInvalidate()
        }

    private val progressPaint by lazy { newStrokePaint { color = progressColor } }

    private var halfWidth = 0f
    private var halfHeight = 0f

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        halfHeight = h / 2f
        halfWidth = w / 2f
    }

    override fun onDraw(canvas: Canvas) {
        canvas.save()
        canvas.addImage(halfWidth, halfHeight, halfWidth)
        canvas.drawRhombus(halfWidth, halfHeight, halfWidth, emptyPaint)
        canvas.drawProgress(halfWidth, halfHeight, width.toFloat(), progressPaint)
        canvas.restore()
    }

    private fun Canvas.drawRhombus(x: Float, y: Float, width: Float, paint: Paint) {
        val path = Path()
        path.moveTo(x, y + width) // Top
        path.lineTo(x - width, y) // Left
        path.lineTo(x, y - width) // Bottom
        path.lineTo(x + width, y) // Right
        path.lineTo(x, y + width) // Back to Top
        path.close()
        drawPath(path, paint)
        path.reset()
    }

    private fun Canvas.addImage(x: Float, y: Float, width: Float) {
        val path = Path()
        path.moveTo(x, y + width) // Top
        path.lineTo(x - width, y) // Left
        path.lineTo(x, y - width) // Bottom
        path.lineTo(x + width, y) // Right
        path.lineTo(x, y + width) // Back to Top
        path.close()
        clipPath(path)
        bitmap?.let { drawBitmap(it, x - it.width / 2, y - it.height / 2, null) }
        path.reset()
    }

    private fun Canvas.drawProgress(x: Float, y: Float, width: Float, paint: Paint) {

        val halfWidth = width / 2
        val length = hypot(x, y)
        val ratio = ((width / 2 / 2 / 2) * 100) / length

        val path = Path()

        path.moveTo(x, y - halfWidth) // Top

        //top to right
        if (progress > 0) {
            val ratioProgress = ratio * (25 - progress)
            val rightProgress = if (progress > 25) 0f else ratioProgress
            path.lineTo(x + halfWidth - rightProgress, y - rightProgress)
        }

        //right to bottom
        if (progress > 25) {
            val ratioProgress = ratio * (50 - progress)
            val bottomProgress = if (progress > 50) 0f else ratioProgress
            path.lineTo(x + bottomProgress, y + halfWidth - bottomProgress)
        }

        //bottom to left
        if (progress > 50) {
            val ratioProgress = ratio * (75 - progress)
            val leftProgress = if (progress > 75) 0f else ratioProgress
            path.lineTo(x - halfWidth + leftProgress, y + leftProgress)
        }

        //left to top
        if (progress > 75) {
            val ratioProgress = ratio * (100 - progress)
            val topProgress = if (progress > 100) 0f else ratioProgress
            path.lineTo(x - topProgress, y - halfWidth + topProgress)
        }

        //finished!
        if (progress >= 100) {
            //path.lineTo(x, y - width)
            path.close()
        }

        drawPath(path, paint)
        path.reset()
    }

    private fun newStrokePaint(block: Paint.() -> Unit = {}) = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeWidth = loadingWidth * 2
        strokeCap = Paint.Cap.BUTT
    }.apply(block)

    private val currentAnimator = ValueAnimator.ofInt().apply {
        duration = 1000
        interpolator = LinearInterpolator()
        addUpdateListener { animation -> progress = animation.animatedValue as Int }
    }

    fun animateTo(destination: Int, current: Int = progress) {
        currentAnimator.cancel()
        currentAnimator.setIntValues(current, destination)
        currentAnimator.start()
    }

    fun animateInterpolator(interpolator: Interpolator) {
        currentAnimator.interpolator = interpolator
    }

    fun animationDuration(durationMs: Long = 1000L) {
        currentAnimator.duration = durationMs
    }

}

fun DiamondLoader.animateTo0() {
    animateTo(0, progress)
}