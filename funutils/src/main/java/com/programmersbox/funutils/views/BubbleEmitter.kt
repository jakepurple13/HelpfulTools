package com.programmersbox.funutils.views

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import android.os.Handler
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.RequiresApi
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import androidx.core.view.contains
import java.util.*
import kotlin.math.abs
import kotlin.random.Random


/**
 * Creates BUBBLES! Adds a view that adds bubbles
 */
@RequiresApi(Build.VERSION_CODES.Q)
class BubbleEmitter @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    init {
        z = 100f
        setBackgroundColor(Color.TRANSPARENT)
        alpha = 1f
    }

    companion object {
        private const val BASE_ALPHA = 255
        private const val NO_VALUE = -1F
        private val WHITE_SMOKE = "#ECECEC".toColorInt()
        private val GHOST_WHITE = "#F9F9F9".toColorInt()

        /**
         * Creates a BubbleView! Make sure to call [BubbleEmitter.on] at some point here
         */
        fun createBubble(context: Context, init: BubbleEmitter.() -> Unit): BubbleEmitter {
            val bubble = BubbleEmitter(context, null)
            bubble.init()
            return bubble
        }

        /**
         * Stops and removes all Bubbles from your #viewGroup
         * @param withAnimation If true, the bubbles will finish their animation. If false, the bubbles will vanish
         */
        fun stopAllBubbles(viewGroup: ViewGroup, withAnimation: Boolean = true) {
            for (i in viewGroup.childCount downTo 0) {
                val v = viewGroup.getChildAt(i)
                if (v != null && v is BubbleEmitter) {
                    v.stopEmitting(true)
                    if (!withAnimation) {
                        if (v.viewGroup != null)
                            viewGroup.removeView(v)
                        else
                            v.removeAllBubbles()
                    }
                }
            }
        }

        /**
         * If you want the bubbles to be clickable to pop them
         */
        val BUBBLE_POP: BubbleEmitter.(MotionEvent?) -> Boolean = { event ->
            val bool = if (event != null) {
                val b1 = bubbleList.find {
                    val x = abs(it.x.toInt() - event.x.toInt())
                    val y = abs(it.y.toInt() - event.y.toInt())
                    (x in 0..10) && (y in 0..10)
                }
                if (b1 != null) {
                    explodeAnimationNow(b1).start()
                    fadeOutAnimationNow(b1).start()
                }
                b1 != null
            } else
                false
            bool
        }

        /**
         * Your custom touch on bubble! The logic to find the bubble is written for you.
         * If you feel that you can do a better logic job, go ahead! This is just to make things a little easier
         */
        fun customBubbleTouch(action: Bubble?.() -> Unit): BubbleEmitter.(MotionEvent?) -> Boolean =
            { event ->
                val bool = if (event != null) {
                    val b1 = bubbleList.find {
                        val x = abs(it.x.toInt() - event.x.toInt())
                        val y = abs(it.y.toInt() - event.y.toInt())
                        (x in 0..10) && (y in 0..10)
                    }
                    b1.action()
                    b1 != null
                } else
                    false
                bool
            }

        /**
         * Bubbles cannot be touched
         */
        val BUBBLE_TOUCH_RESET: BubbleEmitter.(MotionEvent?) -> Boolean = { false }

    }

    class Bubble(
        val uuid: UUID,
        var radius: Float,
        var x: Float = NO_VALUE,
        var y: Float = Float.MAX_VALUE, //Starting y at -1 makes it show up on top for a quick half-second
        var alpha: Int = BASE_ALPHA,
        var alive: Boolean = true,
        var animating: Boolean = false,
        var strokeColor: Int = GHOST_WHITE,
        var fillColor: Int = WHITE_SMOKE,
        var glossColor: Int = Color.WHITE
    )

    /**
     * The outline of the bubble
     */
    val strokeColorsToUse: ArrayList<Int> = arrayListOf(GHOST_WHITE)

    /**
     * The inside of the bubble
     */
    val fillColorsToUse: ArrayList<Int> = arrayListOf(WHITE_SMOKE)

    /**
     * The shine of the bubble
     */
    val glossColorsToUse: ArrayList<Int> = arrayListOf(Color.WHITE)

    private var bubbleLimit = 25

    internal var viewGroup: ViewGroup? = null
    private lateinit var params: ViewGroup.LayoutParams

    private val pushHandler = Handler()
    private var bubbles: MutableList<Bubble> = mutableListOf()

    private var sizeFrom: Int = 20
    private var sizeTo: Int = 80

    private var distance = 2F

    private var movementSpeed = 2000L
        set(value) {
            field = value
            fadeSpeed = value / 2
            explodeSpeed = value / 2
        }
    private var fadeSpeed = 1000L
    private var explodeSpeed = 1000L

    private var emissionDelayMillis: Long = 10L * bubbles.size

    private var timeFrom: Long = 100
    private var timeTo: Long = 500

    private var canExplodes: (Bubble) -> Boolean = { true }

    /**
     * Checks if the current BubbleView is bubbling
     */
    val isBubbling: Boolean get() = emitHandler.hasCallbacks(emitRun)

    private val paintStroke = Paint().apply {
        isAntiAlias = true
        color = GHOST_WHITE
        strokeWidth = 2F
        style = Paint.Style.STROKE
    }

    private val paintFill = Paint().apply {
        isAntiAlias = true
        color = WHITE_SMOKE
        style = Paint.Style.FILL
    }

    private val paintGloss = Paint().apply {
        isAntiAlias = true
        color = ContextCompat.getColor(context, android.R.color.white)
        style = Paint.Style.FILL
    }

    private val emitHandler: Handler = Handler()
    private val emitRun = Runnable {
        emitBubble(Random.nextInt(sizeFrom, sizeTo))
        emit()
    }

    private fun emitRunCustom(num: Int) = Runnable {
        emitBubble(Random.nextInt(sizeFrom, sizeTo), true)
        oneBubble(num - 1)
    }

    /**
     * If you want to change behavior of when you press on the bubbles
     */
    var touchEvent: BubbleEmitter.(MotionEvent?) -> Boolean = BUBBLE_TOUCH_RESET

    /**
     * The current list of bubbles, only use for [touchEvent]
     */
    val bubbleList: MutableList<Bubble>
        get() = bubbles

    private var removeAtEnd = false

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return touchEvent(event)//if (touchEvent(event)) super.onTouchEvent(event) else touchEvent(event)
    }

    override fun onDraw(c: Canvas) {
        super.onDraw(c)

        bubbles = bubbles.filter { it.alive }.toMutableList()

        bubbles.forEach {
            val diameter = (it.radius * 2).toInt()
            if (it.x == -1F) {
                it.x = Random.nextInt(0 + diameter, width - diameter).toFloat()
            }

            paintStroke.alpha = it.alpha
            paintFill.alpha = it.alpha
            paintGloss.alpha = it.alpha

            paintStroke.color = it.strokeColor
            paintFill.color = it.fillColor
            paintGloss.color = it.glossColor

            c.drawCircle(it.x, it.y, it.radius, paintStroke)
            c.drawCircle(it.x, it.y, it.radius, paintFill)
            c.drawCircle(
                it.x + it.radius / 2.5F,
                it.y - it.radius / 2.5F,
                it.radius / 4,
                paintGloss
            )

            if (!it.animating) {
                it.animating = true
                moveAnimation(it.uuid, it.radius).start()
                if (canExplodes(it)) {
                    explodeAnimation(it.uuid, it.radius).start()
                }
                fadeOutAnimation(it.uuid, it.radius).apply {
                    doOnEnd {
                        if (!emitHandler.hasCallbacks(emitRun) && bubbles.size == 0 && removeAtEnd)
                            viewGroup?.removeView(this@BubbleEmitter)
                    }
                }.start()
            }
        }
    }

    /**
     * if you want to modify how the fill is done in anyway
     */
    fun fillModify(modify: (Paint) -> Unit): BubbleEmitter {
        modify(paintFill)
        return this
    }

    /**
     * if you want to modify how the stroke is done in anyway
     */
    fun strokeModify(modify: Paint.() -> Unit): BubbleEmitter {
        paintStroke.modify()
        return this
    }

    /**
     * if you want to modify how the gloss is done in anyway
     */
    fun glossModify(modify: Paint.() -> Unit): BubbleEmitter {
        modify(paintGloss)
        return this
    }

    /**
     * @param viewGroup the layout you want to attach this BubbleEmitter to
     * @param withParams The kind of [ViewGroup.LayoutParams] you want this View to have (Default is [ViewGroup.LayoutParams.MATCH_PARENT])
     */
    fun on(
        viewGroup: ViewGroup,
        withParams: () -> ViewGroup.LayoutParams = {
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
    ): BubbleEmitter {
        this.viewGroup = viewGroup
        this.params = withParams()
        return this
    }

    /**
     * @param withParams The kind of [ViewGroup.LayoutParams] you want this View to have
     */
    fun withParams(withParams: () -> ViewGroup.LayoutParams): BubbleEmitter {
        this.params = withParams()
        return this
    }

    /**
     * Changes the amount of bubbles that can be shown
     */
    fun changeBubbleLimit(bubbleLimit: Int = 25): BubbleEmitter {
        this.bubbleLimit = bubbleLimit
        return this
    }

    /**
     * The size of the bubbles
     */
    fun setSizeRange(sizeFrom: Int = 20, sizeTo: Int = 80): BubbleEmitter {
        this.sizeFrom = sizeFrom
        this.sizeTo = sizeTo
        return this
    }

    /**
     * The size of the bubbles
     */
    fun setSizeRange(range: IntRange = 20..80): BubbleEmitter {
        this.sizeFrom = range.first
        this.sizeTo = range.last
        return this
    }

    /**
     * The distance the bubbles can go
     */
    fun setDistance(distance: Float): BubbleEmitter {
        this.distance = distance
        return this
    }

    /**
     * The time to spawn the bubbles
     */
    fun setTimeRange(
        timeFrom: Long = 100,
        timeTo: Long = 500
    ): BubbleEmitter {
        this.timeFrom = timeFrom
        this.timeTo = timeTo
        return this
    }

    /**
     * The time to spawn the bubbles
     */
    fun setTimeRange(
        range: LongRange = 100L..500L
    ): BubbleEmitter {
        this.timeFrom = range.first
        this.timeTo = range.last
        return this
    }

    /**
     * Starts the BUBBLING!
     */
    fun startEmitting() {
        if (viewGroup != null && !viewGroup!!.contains(this))
            viewGroup!!.addView(this, params)

        if (!emitHandler.hasCallbacks(emitRun))
            emit()
    }

    private fun emit() {
        emitHandler.postDelayed(emitRun, Random.nextLong(timeFrom, timeTo))
    }

    fun oneBubble(num: Int = 10) {
        if (viewGroup != null && !viewGroup!!.contains(this))
            viewGroup!!.addView(this, params)

        if (num > 0) {
            emitHandler.postDelayed(emitRunCustom(num), Random.nextLong(timeFrom, timeTo))
        } else {
            stopEmitting(true)
            return
        }
    }

    /**
     * Stops the bubbling
     * @param removeAtEnd If true, this view will be only removed at the end of the final bubble popping if this was created programmatically.
     * If false, this view will remain.
     */
    fun stopEmitting(removeAtEnd: Boolean = false) {
        this.removeAtEnd = removeAtEnd
        emitHandler.removeCallbacks(emitRun)
    }

    /**
     * this will remove ALL of this view's bubbles and remove the view as well
     */
    fun removeAllBubbles() {
        stopEmitting(true)
        bubbles.clear()
    }

    private fun emitBubble(strength: Int, customRun: Boolean = false) {
        if (bubbles.size >= bubbleLimit) {
            return
        }

        val uuid: UUID = UUID.randomUUID()
        val radius: Float = abs(strength) / 4F
        val bubble = Bubble(
            uuid,
            radius,
            fillColor = fillColorsToUse.random(),
            strokeColor = strokeColorsToUse.random(),
            glossColor = glossColorsToUse.random()
        )

        pushHandler.postDelayed({
            if (isBubbling || customRun)
                bubbles.add(bubble)
        }, emissionDelayMillis)

        invalidate()
    }

    /**
     * sets the colors of the first position
     */
    fun setColors(
        @ColorInt stroke: Int = Color.rgb(249, 249, 249),
        @ColorInt fill: Int = Color.rgb(236, 236, 236),
        @ColorInt gloss: Int = Color.rgb(255, 255, 255)
    ): BubbleEmitter {
        strokeColorsToUse[0] = stroke
        fillColorsToUse[0] = fill
        glossColorsToUse[0] = gloss
        return this
    }

    /**
     * sets the colors of the first position
     */
    fun setColorResources(
        @ColorRes stroke: Int = android.R.color.holo_blue_bright,
        @ColorRes fill: Int = android.R.color.holo_blue_dark,
        @ColorRes gloss: Int = android.R.color.white
    ): BubbleEmitter {
        strokeColorsToUse[0] = context.getColor(stroke)
        fillColorsToUse[0] = context.getColor(fill)
        glossColorsToUse[0] = context.getColor(gloss)
        return this
    }

    /**
     * adds colors
     */
    fun addColors(
        @ColorInt stroke: Int? = null,
        @ColorInt fill: Int? = null,
        @ColorInt gloss: Int? = null
    ): BubbleEmitter {
        stroke?.let { strokeColorsToUse.add(it) }
        fill?.let { fillColorsToUse.add(it) }
        gloss?.let { glossColorsToUse.add(it) }
        return this
    }

    /**
     * adds colors
     */
    fun addResColors(
        @ColorRes stroke: Int? = null,
        @ColorRes fill: Int? = null,
        @ColorRes gloss: Int? = null
    ): BubbleEmitter {
        stroke?.let { strokeColorsToUse.add(context.getColor(it)) }
        fill?.let { fillColorsToUse.add(context.getColor(it)) }
        gloss?.let { glossColorsToUse.add(context.getColor(it)) }
        return this
    }

    /**
     * sets the touch event
     */
    fun setTouchEvent(boolean: Boolean = false) {
        touchEvent = { boolean }
    }

    /**
     * sets the emission delay
     */
    fun setEmissionDelay(delayMillis: Long = 10L * bubbles.size): BubbleEmitter {
        emissionDelayMillis = delayMillis
        return this
    }

    /**
     * if you want the bubbles to "pop"
     */
    fun canExplode(boolean: Boolean = true): BubbleEmitter {
        canExplodes = { boolean }
        return this
    }

    /**
     * a more controlled "pop" allowing only certain bubbles to pop if you want
     */
    fun canExplode(boolean: (Bubble) -> Boolean = { true }): BubbleEmitter {
        canExplodes = boolean
        return this
    }

    /**
     * sets how fast the bubbles move
     */
    fun setMovementSpeed(speed: Long = 2000L): BubbleEmitter {
        this.movementSpeed = speed
        return this
    }

    private fun moveAnimation(uuid: UUID, radius: Float): ValueAnimator {
        val animator: ValueAnimator =
            ValueAnimator.ofFloat(height.toFloat(), height / distance - radius * 10)
        with(animator) {
            addUpdateListener { animation ->
                bubbles.firstOrNull { it.uuid == uuid }?.y = animation.animatedValue as Float
                invalidate()
            }
            duration = movementSpeed + 100L * radius.toLong()
            interpolator = LinearInterpolator()
        }
        return animator
    }

    private fun fadeOutAnimation(uuid: UUID, radius: Float): ValueAnimator {
        val animator: ValueAnimator = ValueAnimator.ofInt(BASE_ALPHA, 0)
        with(animator) {
            addUpdateListener { animation ->
                bubbles.firstOrNull { it.uuid == uuid }?.alpha = animation.animatedValue as Int
            }
            doOnEnd {
                bubbles.firstOrNull { it.uuid == uuid }?.alive = false
                invalidate()
            }
            duration = 200L
            startDelay = fadeSpeed + 100L * radius.toLong()
            interpolator = LinearInterpolator()
        }
        return animator
    }

    private fun explodeAnimation(uuid: UUID, radius: Float): ValueAnimator {
        val animator: ValueAnimator = ValueAnimator.ofFloat(radius, radius * 2)
        with(animator) {
            addUpdateListener { animation ->
                bubbles.firstOrNull { it.uuid == uuid }?.radius = animation.animatedValue as Float
            }
            duration = 300L
            startDelay = explodeSpeed + 100L * radius.toLong()
            interpolator = LinearInterpolator()
        }
        return animator
    }

    /**
     * If you want the bubble to fade immediately
     */
    fun fadeOutAnimationNow(bubble: Bubble): ValueAnimator {
        val animator: ValueAnimator = ValueAnimator.ofInt(BASE_ALPHA, 0)
        with(animator) {
            addUpdateListener { animation ->
                bubbles.firstOrNull { it.uuid == bubble.uuid }?.alpha =
                    animation.animatedValue as Int
            }
            doOnEnd {
                bubbles.firstOrNull { it.uuid == bubble.uuid }?.alive = false
                invalidate()
            }
            duration = 200L
            interpolator = LinearInterpolator()
        }
        return animator
    }

    /**
     * If you want the bubble to pop immediately
     */
    fun explodeAnimationNow(bubble: Bubble): ValueAnimator {
        val animator: ValueAnimator = ValueAnimator.ofFloat(bubble.radius, bubble.radius * 2)
        with(animator) {
            addUpdateListener { animation ->
                bubbles.firstOrNull { it.uuid == bubble.uuid }?.radius =
                    animation.animatedValue as Float
            }
            duration = 300L
            interpolator = LinearInterpolator()
        }
        return animator
    }
}

/**
 * creates and attaches a BubbleView to this [ViewGroup]
 */
@RequiresApi(Build.VERSION_CODES.Q)
fun ViewGroup.createBubbles(init: BubbleEmitter.() -> Unit): BubbleEmitter {
    val bubble = BubbleEmitter(context, null)
    bubble.on(this).init()
    return bubble
}

/**
 * @see BubbleEmitter.stopAllBubbles
 */
fun ViewGroup.stopAllBubbles(withAnimation: Boolean = true) =
    BubbleEmitter.stopAllBubbles(this, withAnimation)

/**
 * Have fun with bubbles! This will create 360 bubble views rotating at all 360 degrees
 */
@RequiresApi(Build.VERSION_CODES.Q)
fun ViewGroup.bubbleParty(numOfColors: Int = 20, init: BubbleEmitter.() -> Unit = {}) {
    for (i in 0..360) {
        createBubbles {
            for (j in 0..numOfColors) {
                fillColorsToUse += (Math.random() * 16777215).toInt() or (0xFF shl 24)
            }
            rotation = i.toFloat()
            init(this)
        }.startEmitting()
    }
}