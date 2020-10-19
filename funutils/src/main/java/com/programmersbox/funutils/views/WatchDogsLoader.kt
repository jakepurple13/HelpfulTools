package com.programmersbox.funutils.views

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.programmersbox.funutils.R
import kotlin.math.pow
import kotlin.math.sqrt


class WatchDogsLoader : View {
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
        val a = context.obtainStyledAttributes(attrs, R.styleable.WatchDogsLoader)
        progress = a.getInt(R.styleable.WatchDogsLoader_progress, if (isInEditMode) 75 else 0)
        loadingWidth = a.getDimension(R.styleable.WatchDogsLoader_lineWidth, 5f)
        progressColor = a.getColor(R.styleable.WatchDogsLoader_progressColor, Color.BLUE)
        emptyColor = a.getColor(R.styleable.WatchDogsLoader_emptyColor, Color.DKGRAY)
        bitmap = a.getDrawable(R.styleable.WatchDogsLoader_src)?.toBitmap()
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
            field = value
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

    private var listener: LoaderListener? = null

    fun setAnimationListener(listener: LoaderListener?) {
        this.listener = listener
    }

    private var halfWidth = 0f
    private var halfHeight = 0f

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        halfHeight = h / 2f
        halfWidth = w / 2f
    }

    override fun onDraw(canvas: Canvas) {
        canvas.save()

        canvas.drawRhombus(
            halfWidth,
            halfHeight,
            halfWidth,
            emptyPaint
        )

        canvas.drawProgress(
            halfWidth,
            halfHeight,
            width.toFloat(),
            progressPaint
        )

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
        clipPath(path)
        bitmap?.let { drawBitmap(it, x - it.width / 2, y - it.height / 2, null) }
    }

    private fun Canvas.drawProgress(x: Float, y: Float, width: Float, paint: Paint) {

        val halfWidth = width / 2

        val path = Path()

        //val fullWidth = width * 2
        val length = sqrt(width.pow(2) + y.pow(2))
        val ratio = ((width / 2 / 2) * 100) / length

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
            listener?.onComplete()
        }

        drawPath(path, paint)
    }

    private fun newStrokePaint(block: Paint.() -> Unit = {}) = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeWidth = loadingWidth
        strokeCap = Paint.Cap.ROUND
    }.apply(block)

    fun interface LoaderListener {
        fun onComplete()
    }

}