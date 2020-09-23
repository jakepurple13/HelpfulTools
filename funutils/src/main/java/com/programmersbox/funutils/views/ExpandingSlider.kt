package com.programmersbox.funutils.views


import android.animation.Animator
import android.animation.Animator.AnimatorListener
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.*
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import com.programmersbox.funutils.R
import com.programmersbox.funutils.views.ExpandingSlider.SliderListener
import java.text.DecimalFormat
import kotlin.math.max
import kotlin.math.sqrt

/**
 * Created by iroyo on 22/8/15.
 * Modified by jakepurple13 on 6/3/20
 * Library
 */

class ExpandingSlider : View {
    private var pSlider: Paint? = null
    private var pIndicator: Paint? = null
    private var pBase: Paint? = null
    private var pTitle: Paint? = null
    private var pValue: Paint? = null
    private val hFactor = 10
    private var hTopSlider = 0f
    private var hBottomSlider = 0f
    private var marginLeft = 0f
    private var marginRight = 0f
    var max = 0f
    var min = 0f
    private var value = 0f
    private var stepSize = 0f
    private var preciseStepSize = 0f
    private var decimals = 0
    var unit: String? = ""
    private var result = ""
    private var resultSize = 0f
    private var valueFormat: DecimalFormat? = null
    private var showAnimation = true
    var showInitialValue = true
    var showIndicator = false
    private var isAnimating = false
    private var position = 0f
    private var prevPosition = 0f
    private var prevWidthCanvas = 0f
    var title: String? = ""
    private var titleSize = 0f
    private var widthCanvas = 0
    private var heightCanvas = 0
    private var listener: SliderListener? = null
    private var slideUpAnimation: ObjectAnimator? = null
    private var slideDownAnimation: ObjectAnimator? = null
    private var sliderSize: Float = 0f
    private var indicatorSize: Float = 20f
    private var isUp = false
    private var isMoving = false

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(attrs)
    }

    /**
     * get a color from the theme
     */
    private fun Context.colorFromTheme(@AttrRes colorAttr: Int, @ColorInt defaultColor: Int = Color.BLACK): Int = TypedValue().run typedValue@{
        this@colorFromTheme.theme.resolveAttribute(colorAttr, this@typedValue, true).run { if (this) data else defaultColor }
    }

    private fun init(attrs: AttributeSet?) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.ExpandingSlider)

        // GET VALUES FROM XML
        showAnimation = a.getBoolean(R.styleable.ExpandingSlider_slider_showAnimation, true)
        showInitialValue = a.getBoolean(R.styleable.ExpandingSlider_slider_showValue, true)
        showIndicator = a.getBoolean(R.styleable.ExpandingSlider_slider_showIndicator, false)
        showBubble = a.getBoolean(R.styleable.ExpandingSlider_slider_showBubble, true)
        stepSize = a.getFloat(R.styleable.ExpandingSlider_slider_stepSize, 0.5f)
        preciseStepSize = a.getFloat(R.styleable.ExpandingSlider_slider_preciseStepSize, 0.1f)
        decimals = a.getInteger(R.styleable.ExpandingSlider_slider_decimals, 0)
        marginLeft = a.getDimension(R.styleable.ExpandingSlider_slider_marginLeft, 12f)
        marginRight = a.getDimension(R.styleable.ExpandingSlider_slider_marginRight, 12f)
        resultSize = a.getDimension(R.styleable.ExpandingSlider_slider_resultSize, 18f)
        titleSize = a.getDimension(R.styleable.ExpandingSlider_slider_titleSize, 20f)
        title = a.getString(R.styleable.ExpandingSlider_slider_title)
        sliderSize = a.getDimension(R.styleable.ExpandingSlider_slider_sliderSize, 0f)
        indicatorSize = a.getDimension(R.styleable.ExpandingSlider_slider_indicatorSize, 20f)
        value = a.getFloat(R.styleable.ExpandingSlider_slider_initialValue, 20f)
        unit = a.getString(R.styleable.ExpandingSlider_slider_unit)
        max = a.getFloat(R.styleable.ExpandingSlider_slider_maxValue, 100f)
        min = a.getFloat(R.styleable.ExpandingSlider_slider_minValue, 0f)
        mBubbleRadius = a.getDimension(R.styleable.ExpandingSlider_slider_bubbleRadius, 50f).toInt()
        mBubbleTextColor = a.getColor(R.styleable.ExpandingSlider_slider_bubbleTextColor, context.colorFromTheme(R.attr.titleTextColor, Color.BLACK))
        mBubbleTextSize = a.getDimension(R.styleable.ExpandingSlider_slider_bubbleTextSize, 20f)
        mProgressText = a.getString(R.styleable.ExpandingSlider_slider_bubbleText) ?: ""
        val resultColor = a.getColor(R.styleable.ExpandingSlider_slider_resultColor, context.colorFromTheme(R.attr.titleTextColor, Color.BLACK))
        val titleColor = a.getColor(R.styleable.ExpandingSlider_slider_titleColor, context.colorFromTheme(R.attr.titleTextColor, Color.BLACK))
        val colorBase = a.getColor(R.styleable.ExpandingSlider_slider_colorBase, context.colorFromTheme(R.attr.colorControlHighlight, Color.GRAY))
        val colorMain = a.getColor(R.styleable.ExpandingSlider_slider_colorMain, context.colorFromTheme(R.attr.colorAccent, Color.CYAN))

        mBubbleColor = a.getColor(R.styleable.ExpandingSlider_slider_bubbleColor, colorMain)

        // ORDERS IS IMPORTANT
        initValueFormat(decimals)

        // INITIALIZE CANVAS
        pSlider = Paint(Paint.ANTI_ALIAS_FLAG)
        pSlider!!.style = Paint.Style.FILL
        pSlider!!.color = colorMain
        pIndicator = Paint(Paint.ANTI_ALIAS_FLAG)
        pIndicator!!.style = Paint.Style.FILL
        pIndicator!!.color = Utils.darken(colorMain, 0.65f)
        pBase = Paint(Paint.ANTI_ALIAS_FLAG)
        pBase!!.style = Paint.Style.FILL
        pBase!!.color = colorBase
        pTitle = Paint(Paint.ANTI_ALIAS_FLAG)
        pTitle!!.textAlign = Paint.Align.LEFT
        pTitle!!.style = Paint.Style.STROKE
        pTitle!!.color = titleColor
        pTitle!!.textSize = titleSize
        pValue = Paint(Paint.ANTI_ALIAS_FLAG)
        pValue!!.textAlign = Paint.Align.RIGHT
        pValue!!.style = Paint.Style.STROKE
        pValue!!.color = resultColor
        pValue!!.textSize = resultSize
        a.recycle()
    }

    public override fun onSaveInstanceState(): Parcelable? {
        val bundle = Bundle()
        bundle.putParcelable("instanceState", super.onSaveInstanceState())
        bundle.putFloat("position", position)
        bundle.putFloat("width", widthCanvas.toFloat())
        return bundle
    }

    public override fun onRestoreInstanceState(state: Parcelable) {
        var state1: Parcelable? = state
        if (state1 is Bundle) {
            val bundle = state1
            prevPosition = bundle.getFloat("position")
            prevWidthCanvas = bundle.getFloat("width")
            state1 = bundle.getParcelable("instanceState")
        }
        super.onRestoreInstanceState(state1)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        widthCanvas = w
        heightCanvas = h
        hTopSlider = h - h * hFactor / 100.toFloat()
        hBottomSlider = h.toFloat()
        position = w * prevPosition / prevWidthCanvas
        updateResult()
        initAnimation()
        super.onSizeChanged(w, h, oldw, oldh)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawBase(canvas)
        drawSlider(canvas)
        if (showInitialValue) drawValue(canvas)
        if (showIndicator && !isAnimating) drawIndicator(canvas)
        if (title != null) drawTitle(canvas)
        if (isMoving && showBubble) drawBubble(canvas)
    }

    //DRAWS ----------------------------------------------------------------------------------------
    private fun <T : Number> T.isUp() = if (isUp) this.toFloat() else (this.toFloat() / 2)
    private val slideHeight get() = sliderSize / 2
    private val topOfSlider get() = hTopSlider.isUp() - slideHeight
    private val bottomOfSlider get() = hBottomSlider.isUp() + slideHeight

    private fun drawSlider(c: Canvas) = c.drawRect(0f, topOfSlider, position, bottomOfSlider, pSlider!!)
    private fun drawIndicator(c: Canvas) {
        c.drawCircle(position, hTopSlider.isUp() + slideHeight, indicatorSize, pIndicator!!)
        if (isUp) c.drawCircle(position, hBottomSlider.isUp() - slideHeight, indicatorSize, pIndicator!!)
    }

    private fun drawBase(c: Canvas) = c.drawRect(0f, topOfSlider, widthCanvas.toFloat(), bottomOfSlider, pBase!!)

    private fun drawValue(c: Canvas) = c.drawText("$result $unit", widthCanvas - marginRight, heightCanvas / 2 + resultSize / 2, pValue!!)
    private fun drawTitle(c: Canvas) = c.drawText(title!!, marginLeft, heightCanvas / 2 + titleSize / 2, pTitle!!)

    // SETTERS & GETTERS ---------------------------------------------------------------------------

    var colorBase
        get() = pBase!!.color
        set(value) = run { pBase!!.color = value }

    var colorMain
        get() = pSlider!!.color
        set(value) = run { pSlider!!.color = value }

    var titleColor
        get() = pTitle!!.color
        set(value) = run { pTitle!!.color = value }

    fun setResultSize(resultSize: Float) = run { pValue!!.textSize = resultSize }
    fun setResultColor(resultColor: Int) = run { pValue!!.color = resultColor }

    fun setDecimals(decimals: Int) {
        this.decimals = decimals
        initValueFormat(decimals)
    }

    fun setListener(listener: SliderListener?) = run { this.listener = listener }

    fun setListener(block: ((Float, View?) -> Unit)?) {
        this.listener = block?.let { SliderListener { value, v -> it(value, v) } }
    }

    var heightSlider: Float
        get() = hTopSlider
        set(hSlider) {
            this.hTopSlider = hSlider
            invalidate()
        }

    fun setValue(value: Float) {
        if (value in min..max) {
            this.value = value
            updateResult()
        }
    }

    fun getValue(): Float = value

    val maxDigits: Int get() = max.toInt().toString().length
    fun getDecimals(): Int = decimals

    private fun updateResult() {
        result = valueFormat!!.format(value.toDouble())
        if (listener != null) listener!!.onValueChanged(value, this)
        val absolute = value * 100 / (max - min)
        val percentage = if (min > 0) absolute - max else absolute
        position = percentage * widthCanvas / 100
        invalidate()
    }

    private fun initValueFormat(digits: Int) {
        val b = StringBuilder()
        for (i in 0 until digits) {
            if (i == 0) b.append(".")
            b.append("0")
        }
        valueFormat = DecimalFormat("###,###,###,##0$b")
    }

    private fun initAnimation() {
        slideUpAnimation = ObjectAnimator.ofFloat(this, "heightSlider", 0f, hTopSlider).setDuration(50)
        slideUpAnimation!!.interpolator = LinearInterpolator()
        slideUpAnimation!!
        slideUpAnimation!!.addListener(object : AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                isAnimating = true
            }

            override fun onAnimationEnd(animation: Animator) {}

            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })
        slideDownAnimation = ObjectAnimator.ofFloat(this, "heightSlider", hTopSlider).setDuration(250)
        slideDownAnimation!!.interpolator = DecelerateInterpolator()
        slideDownAnimation!!.addListener(object : AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}

            override fun onAnimationEnd(animation: Animator) {
                isAnimating = false
            }

            override fun onAnimationCancel(animation: Animator) {
                isAnimating = false
            }

            override fun onAnimationRepeat(animation: Animator) {}
        })
    }

    private fun showLarger(motionEvent: MotionEvent) = motionEvent.let {
        //println("${it.rawY} and $y and $height")
        val yCheck = (y + height) - topOfSlider
        when {
            //slide up here
            //increase height to a point here
            it.rawY < yCheck -> true
            //go back to normal
            //put height back to normal here
            it.rawY > yCheck -> false
            else -> false
        }
    }

    override fun onTouchEvent(e: MotionEvent): Boolean {
        val x = e.x
        when (e.action) {
            MotionEvent.ACTION_DOWN -> Unit//if (showAnimation) slideUpAnimation!!.start() else hSlider = 0f
            MotionEvent.ACTION_UP -> {
                isMoving = false
                //if (showAnimation) slideUpAnimation!!.cancel()
                //if (showAnimation) slideDownAnimation!!.start() else hTopSlider = heightCanvas - heightCanvas * hFactor / 100.toFloat()
                isUp = false
                hTopSlider = heightCanvas - heightCanvas * hFactor / 100.toFloat()
                if (x != position) updateValue(x)
            }
            MotionEvent.ACTION_MOVE -> {
                isMoving = true
                /*val (cancel, start) = when {
                    showLarger(e) -> {
                        isUp = true
                        slideDownAnimation to slideUpAnimation
                    }
                    isUp -> {
                        isUp = false
                        slideUpAnimation to slideDownAnimation
                    }
                    else -> null to null
                }*/
                /*if (showAnimation) {
                    cancel?.cancel()
                    start?.start()
                } else {*/
                //hTopSlider = if (isUp) 0f else heightCanvas - heightCanvas * hFactor / 100.toFloat()
                //}
                //bubbleView?.x = e.x

                when {
                    showLarger(e) -> isUp = true
                    isUp -> isUp = false
                }

                hTopSlider = if (isUp) 0f else heightCanvas - heightCanvas * hFactor / 100.toFloat()
                if (x != position) updateValue(x)
            }
            else -> Unit
        }
        invalidate()
        return true
    }

    private fun updateValue(x: Float) {
        if (x >= 0 && x <= widthCanvas) {
            val step = if (isUp) preciseStepSize else stepSize
            position = x
            val percentage = position * 100 / widthCanvas
            val absolute = percentage * (max - min) / 100
            val total = absolute + min
            val remainder = total % step
            value = if (remainder <= step / 2f) total - remainder else total - remainder + step
            value = if (value < min) min else if (value > max) max else value
            result = valueFormat!!.format(value.toDouble())
            if (listener != null) listener!!.onValueChanged(value, this)
            setProgressText("$result $unit")
        }
    }

    //INTERFACE
    fun interface SliderListener {
        fun onValueChanged(value: Float, v: View?)
    }

    // UTILITY CLASS
    object Utils {
        fun darken(color: Int, factor: Float): Int = Color.argb(
            Color.alpha(color),
            max((Color.red(color) * factor).toInt(), 0),
            max((Color.green(color) * factor).toInt(), 0),
            max((Color.blue(color) * factor).toInt(), 0)
        )
    }

    //bubble
    private val mBubblePaint: Paint = Paint()
    private val mBubblePath: Path
    private val mBubbleRectF: RectF
    private var mBubbleRadius = 50
    private var mBubbleColor = Color.CYAN
    private var mBubbleTextColor = Color.BLACK
    private var mBubbleTextSize = 25f
    private val mRect: Rect
    private var mProgressText = ""
    var showBubble: Boolean = true

    init {
        mBubblePaint.isAntiAlias = true
        mBubblePaint.textAlign = Paint.Align.CENTER
        mBubblePath = Path()
        mBubbleRectF = RectF()
        mRect = Rect()
    }

    private fun drawBubble(canvas: Canvas) {
        val arrowPosition = hTopSlider.isUp()
        mBubbleRectF.set(
            position - mBubbleRadius,
            arrowPosition - measuredHeight,
            position + mBubbleRadius,
            2 * mBubbleRadius.toFloat() - measuredHeight + hTopSlider.isUp() + slideHeight
        )
        mBubblePath.reset()
        val x0 = position
        val y0: Float = arrowPosition//measuredHeight - mBubbleRadius / 3f
        mBubblePath.moveTo(x0, y0)
        val x1 = (position - sqrt(3.0) / 2f * mBubbleRadius).toFloat()
        val y1: Float = arrowPosition - 3 / 2f * mBubbleRadius
        mBubblePath.quadTo(
            x1, y1,
            x1, y1
        )
        mBubblePath.arcTo(mBubbleRectF, 150f, 240f)
        val x2 = (position + sqrt(3.0) / 2f * mBubbleRadius).toFloat()
        mBubblePath.quadTo(
            x2, y1,
            x0, y0
        )
        mBubblePath.close()
        mBubblePaint.color = mBubbleColor
        canvas.drawPath(mBubblePath, mBubblePaint)
        mBubblePaint.textSize = mBubbleTextSize
        mBubblePaint.color = mBubbleTextColor
        mBubblePaint.getTextBounds(mProgressText, 0, mProgressText.length, mRect)
        val fm = mBubblePaint.fontMetrics
        val baseline: Float = arrowPosition - 2f * mBubbleRadius + (fm.descent - fm.ascent) / 2f - fm.descent
        canvas.drawText(mProgressText, position, baseline, mBubblePaint)
    }

    private fun setProgressText(progressText: String?) {
        if (progressText != null && mProgressText != progressText) {
            mProgressText = progressText
            invalidate()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(3 * mBubbleRadius, 3 * mBubbleRadius)
    }

}