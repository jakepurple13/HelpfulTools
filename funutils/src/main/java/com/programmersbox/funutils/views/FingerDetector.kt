package com.programmersbox.funutils.views

import android.annotation.SuppressLint
import android.graphics.PointF
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.view.MotionEvent
import android.view.View
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

class FingerDetector @JvmOverloads constructor(
    numberOfFingers: Int = 2,
    holdDownDelay: Long = 100L,
    upDelay: Long = 50L,
    /** whether to consume the touch event after handling*/
    var isConsumeTouchEvents: Boolean = true,
    var onFingerListener: FingerListener? = null
) : View.OnTouchListener {
    /** number of allowed fingers, that will be detected*/
    var numberOfFingers: Int = 2
        set(value) {
            field = value
            handler.removeCallbacksAndMessages(null)
            fingers = Array(value) { Finger() }
            // set twice as many runnables, as the number of allowed fingers, for 4-Runnable => 2-HoldDown + 2-Up
            val tempRunnables = arrayOfNulls<ChangeState?>(value * 2)
            // runnable for changing finger state to -HOLD_DOWN or -UP
            for (i in 0 until value) {
                tempRunnables[i] = ChangeState(i, State.HOLD_DOWN) // first half for hold down
                tempRunnables[i + value] = ChangeState(i, State.UP) // second half for up
            }
            runnables = tempRunnables.requireNoNulls()
        }

    /** delay time after which if finger is -hold down, state will be changed to HOLD_DOWN*/
    var holdDownDelay: Long = holdDownDelay
        set(value) {
            field = value
            handler.removeCallbacksAndMessages(null)
        }

    /** delay time after which if finger is -swiped, state will be changed to UP*/
    var upDelay: Long = upDelay
        set(value) {
            field = value
            handler.removeCallbacksAndMessages(null)
        }

    private var fingers: Array<Finger> = arrayOf()
    private val handler: Handler = Handler(Looper.getMainLooper())
    private var runnables: Array<ChangeState> = arrayOf()

    init {
        this.numberOfFingers = numberOfFingers
    }

    /**
     * Called on ACTION_DOWN || ACTION_POINTER_DOWN events, to detect and set
     * new current state, for a certain finger. Remove delay callback for
     * up state and set new one for hold-down state, and finally call listener method.
     * @param event        motion event from the onTouch event
     * @param arrayIndex   index corresponding to consecutive finger on screen
     * @param pointerIndex pointer index used, to get current finger position
     */
    private fun down(event: MotionEvent, arrayIndex: Int, pointerIndex: Int) {

        // set finger state and tracking
        fingers[arrayIndex].tracking = true
        fingers[arrayIndex].detectState(event, pointerIndex)

        // remove callback for change current state to -up
        handler.removeCallbacks(runnables[arrayIndex + fingers.size])

        // post a delay callback for detecting hold-down state
        handler.postDelayed(runnables[arrayIndex], holdDownDelay)

        // call listener method, fot state change
        onFingerListener?.onStateChange(fingers, arrayIndex)
    }

    /**
     * Called on ACTION_UP || ACTION_POINTER_UP events, to detect and set
     * new current state, for a certain finger. Remove delay callback for
     * hold-down state and set new one for up state, and finally call listener method.
     * @param event motion event from the onTouch event
     * @param arrayIndex index corresponding to consecutive finger on screen
     * @param pointerIndex pointer index used, to get current finger position
     */
    private fun up(event: MotionEvent, arrayIndex: Int, pointerIndex: Int) {
        // set finger state and tracking
        fingers[arrayIndex].tracking = false
        fingers[arrayIndex].detectState(event, pointerIndex)

        // set callback for up state, if swipe or double tap event is made!!!
        val state = fingers[arrayIndex].stateCurrent

        if (state == State.SWIPE_DOWN || state == State.SWIPE_LEFT || state == State.SWIPE_RIGHT || state == State.SWIPE_UP || state == State.DOUBLE_TAP) {
            handler.postDelayed(runnables[arrayIndex + fingers.size], upDelay)
        }

        // remove listener callback for hold-down state
        handler.removeCallbacks(runnables[arrayIndex])

        // call listener method for state change
        onFingerListener?.onStateChange(fingers, arrayIndex)
    }

    private fun move(event: MotionEvent) {
        val num = event.pointerCount
        for (pointerIndex in 0 until num) {
            val arrayIndex = event.getPointerId(pointerIndex) // id corresponding to array index
            // if it is being tracked
            if (arrayIndex < fingers.size && fingers[arrayIndex].tracking) {
                // get last and current state
                fingers[arrayIndex].detectState(event, pointerIndex)
                // call only if state is changed
                if (fingers[arrayIndex].stateLast != fingers[arrayIndex].stateCurrent) {
                    onFingerListener?.onStateChange(fingers, arrayIndex)
                }
                // if actual move is made reset the callback for the hold-down
                if (fingers[arrayIndex].updateLast) {
                    handler.removeCallbacks(runnables[arrayIndex])
                    handler.postDelayed(runnables[arrayIndex], holdDownDelay)
                }
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        val pointerIndex = event.actionIndex
        val arrayIndex = event.getPointerId(pointerIndex) // corresponds to array index, since it is const.
        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                if (arrayIndex < fingers.size)
                    down(event, arrayIndex, pointerIndex)
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {
                if (arrayIndex < fingers.size)
                    up(event, arrayIndex, pointerIndex)
            }
            MotionEvent.ACTION_MOVE -> move(event)
        }
        return isConsumeTouchEvents
    }

    /**
     * Runnable implementation class that hold the array index, and the new state
     * for the corresponding finger. Runnable is set for delay callback, for changing
     * finger state, whenever a state should be changed after a delay timeout.
     */
    internal inner class ChangeState(private val arrayIndex: Int, private val newState: State) : Runnable {
        override fun run() {
            // set last state, before changing current
            fingers[arrayIndex].stateLast = fingers[arrayIndex].stateCurrent
            fingers[arrayIndex].stateCurrent = newState
            onFingerListener?.onStateChange(fingers, arrayIndex)
        }
    }

    /**
     * Public interface with one method, that can be implemented and listen for
     * fingers state changes.
     */
    fun interface FingerListener {
        /**
         * Called when finger state is changed, first arguments hold the array with
         * all finger objects, second argument is the finger index showing which
         * finger has a state change.
         * @param fingers array with all finger objects
         * @param fingerIndex finger index whose state is changed
         */
        fun onStateChange(fingers: Array<Finger>, fingerIndex: Int)
    }

    enum class State(private val string: String) {
        NONE("NONE"),
        SWIPE_UP("SWIPE UP"), SWIPE_DOWN("SWIPE DOWN"), SWIPE_LEFT("SWIPE LEFT"), SWIPE_RIGHT("SWIPE RIGHT"),
        HOLD_DOWN("HOLD DOWN"),
        DOWN("DOWN"), UP("UP"),
        MOVE_UP("MOVE UP"), MOVE_DOWN("MOVE DOWN"), MOVE_LEFT("MOVE LEFT"), MOVE_RIGHT("MOVE RIGHT"),
        DOUBLE_TAP("DOUBLE TAP");

        override fun toString(): String = string
    }

}

class Finger(
    /**min distance the finger must travel, before swipe event can be detected*/
    var minDistanceSwipe: Int,
    /**max time after which the swipe event WILL NOT be detected (ms)*/
    var maxDurationSwipe: Int,
    /**min distance the finger must travel, before move event can be detected*/
    var minDistanceMove: Int,
    /**max delay time between the two -down events for the double tap (ms)*/
    var maxDurationDoubleTap: Int,
    /**max time the finger can be hold down for the two -down events (ms)*/
    var maxDownDoubleTap: Int,
    /**slope intolerance for swipe and move*/
    var slopeIntolerance: Double,
    /**current finger state - down, up, swipe_left...*/
    var stateCurrent: FingerDetector.State,
    /**previous finger state*/
    var stateLast: FingerDetector.State,
    /**distance between -positionCurrent and -positionInitial*/
    var distanceInitial: Double,
    /**distance between -positionCurrent and -positionLast*/
    var distanceLast: Double,
    /**duration between -ACTION_DOWN and current event*/
    var durationInitial: Double,
    /**duration between the previous and current event*/
    var durationLast: Double,
    /**initial time when the finger is pressed down from -ACTION_DOWN event*/
    var timeInitial: Long,
    /**previously detected time from -ACTION_UP, ACTION_MOVE events*/
    var timeLast: Long,
    /**current detected time from -ACTION_UP, ACTION_MOVE events*/
    var timeCurrent: Long,
    /**if finger is tracked set from -ACTION_UP(false) and -ACTION_DOWN(true) events*/
    var tracking: Boolean,
    /**if -positionLast should be updated from current event*/
    var updateLast: Boolean,
    positionDeltaInitial: PointF, positionDeltaLast: PointF, positionInitial: PointF, positionLast: PointF, positionCurrent: PointF
) {
    /**difference between -positionCurrent and -positionInitial*/
    var positionDeltaInitial: PointF = positionDeltaInitial.copy()
        private set

    /**difference between -positionCurrent and -positionLast*/
    var positionDeltaLast: PointF = positionDeltaLast.copy()
        private set

    /**initial finger position when it is pressed down for -ACTION_DOWN event*/
    var positionInitial: PointF = positionInitial.copy()
        private set

    /**previous finger position from -ACTION_UP, ACTION_MOVE events*/
    var positionLast: PointF = positionLast.copy()
        private set

    /**current finger position from  -ACTION_UP, ACTION_MOVE events*/
    var positionCurrent: PointF = positionCurrent.copy()
        private set

    /**last finger object from previous event, used to detect double tap*/
    var lastFinger: Finger? = null
        private set

    @JvmOverloads
    constructor(
        minDistanceSwipe: Int = MIN_DISTANCE_SWIPE, maxDurationSwipe: Int = MAX_DURATION_SWIPE,
        minDistanceMove: Int = MIN_DISTANCE_MOVE,
        maxDurationDoubleTap: Int = MAX_DURATION_DOUBLE_TAP, maxDownDoubleTap: Int = MAX_DOWN_DOUBLE_TAP,
        slopeIntolerance: Int = SLOPE_INTOLERANCE
    ) : this(
        minDistanceSwipe, maxDurationSwipe, minDistanceMove,
        maxDurationDoubleTap, maxDownDoubleTap, slopeIntolerance.toDouble(), FingerDetector.State.NONE,
        FingerDetector.State.NONE, 0.0, 0.0, 0.0, 0.0,
        0, 0, 0, false, true, PointF(), PointF(),
        PointF(), PointF(), PointF()
    )

    constructor(f: Finger) : this(
        f.minDistanceSwipe, f.maxDurationSwipe, f.minDistanceMove, f.maxDurationDoubleTap,
        f.maxDownDoubleTap, f.slopeIntolerance, f.stateCurrent, f.stateLast, f.distanceInitial,
        f.distanceLast, f.durationInitial, f.durationLast, f.timeInitial, f.timeLast,
        f.timeCurrent, f.tracking, f.updateLast, f.positionDeltaInitial,
        f.positionDeltaLast, f.positionInitial, f.positionLast, f.positionCurrent
    )

    private fun PointF.copy(): PointF = PointF(x, y)

    private fun reset() {
        stateCurrent = FingerDetector.State.NONE
        stateLast = FingerDetector.State.NONE
        distanceInitial = 0.0
        distanceLast = 0.0
        durationInitial = 0.0
        durationLast = 0.0
        timeInitial = 0
        timeLast = 0
        timeCurrent = 0
        tracking = false
        updateLast = true
        positionDeltaInitial = PointF()
        positionDeltaLast = PointF()
        positionInitial = PointF()
        positionLast = PointF()
        positionCurrent = PointF()
    }

    /**
     * Determine if double tap is made using initial time from both finger
     * object -current and -last to determine gesture duration and use initial
     * duration from both finger objects to determine the finger down delay.
     * And check if those values are in allowed ranges.
     * @return
     */
    val isDoubleTap: Boolean
        get() {
            if (lastFinger == null) return false
            return timeInitial - lastFinger!!.timeInitial < maxDurationDoubleTap && durationInitial < maxDownDoubleTap && lastFinger!!.durationInitial < maxDownDoubleTap
        }

    /**
     * Method that determine the new state and sets current finger object
     * state value.
     * @param event        onTouch motion event
     * @param pointerIndex pointer index used, to get current finger position
     */
    fun detectState(event: MotionEvent, pointerIndex: Int) {
        stateLast = stateCurrent
        val action = event.action and MotionEvent.ACTION_MASK
        if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_DOWN) {
            // when finger is pressed down
            setInitial(event, pointerIndex)
            stateCurrent = FingerDetector.State.DOWN
        } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_POINTER_UP) {
            updateLast = true

            // when finger is lift up
            update(event, pointerIndex)
            if (isDoubleTap) {
                stateCurrent = FingerDetector.State.DOUBLE_TAP
                return
            }
            if (abs(positionDeltaInitial.x) < minDistanceSwipe && abs(positionDeltaInitial.y) < minDistanceSwipe || durationInitial > maxDurationSwipe) {
                // if minimum distance is not reached or the maximum time is passed, swipe is NOT detected
                stateCurrent = FingerDetector.State.UP
            } else {
                // determine the swipe direction
                val x = positionDeltaInitial.x
                val y = positionDeltaInitial.y
                when {
                    -y > slopeIntolerance * abs(x) -> FingerDetector.State.SWIPE_UP
                    y > slopeIntolerance * abs(x) -> FingerDetector.State.SWIPE_DOWN
                    -x > slopeIntolerance * abs(y) -> FingerDetector.State.SWIPE_LEFT
                    x > slopeIntolerance * abs(y) -> FingerDetector.State.SWIPE_RIGHT
                    else -> null
                }?.let { stateCurrent = it }
            }
        } else if (action == MotionEvent.ACTION_MOVE) {
            // when finger is moved
            update(event, pointerIndex)

            // check if finger moved to minimum distance before, detecting the move state
            if (abs(positionDeltaLast.x) < minDistanceMove && abs(positionDeltaLast.y) < minDistanceMove) {
                updateLast = false
                return
            } else {
                updateLast = true
            }

            // determine the direction
            val x = positionDeltaLast.x
            val y = positionDeltaLast.y
            when {
                -y > slopeIntolerance * abs(x) -> FingerDetector.State.MOVE_UP
                y > slopeIntolerance * abs(x) -> FingerDetector.State.MOVE_DOWN
                -x > slopeIntolerance * abs(y) -> FingerDetector.State.MOVE_LEFT
                x > slopeIntolerance * abs(y) -> FingerDetector.State.MOVE_RIGHT
                else -> null
            }?.let { stateCurrent = it }
        }
    }

    /**
     * Event that is called when finger is pressed down. Method saves
     * finger object values to -lastFinger object before resetting current
     * finger values to default. Than set initial time and finger position.
     *
     * @param event        - onTouch motion event
     * @param pointerIndex - finger index whose state will be changed
     */
    fun setInitial(event: MotionEvent, pointerIndex: Int) {
        lastFinger = Finger(this) // save current finger object
        reset() // reset current

        // set initial time and position
        positionInitial = PointF(event.getX(pointerIndex), event.getY(pointerIndex))
        timeInitial = SystemClock.uptimeMillis()
        tracking = true // since it is called from down event and tracking has began
    }

    /**
     * Update previous and current time, finger position,
     * delta, distance and duration for the gesture
     *
     * @param event        - onTouch motion event
     * @param pointerIndex - finger index whose state will be changed
     */
    fun update(event: MotionEvent, pointerIndex: Int) {
        if (updateLast) {
            positionLast = PointF(positionCurrent.x, positionCurrent.y)
            timeLast = timeCurrent
        }
        positionCurrent = PointF(event.getX(pointerIndex), event.getY(pointerIndex))
        timeCurrent = SystemClock.uptimeMillis()

        // delta and distance between -positionCurrent and -positionLast
        positionDeltaLast = PointF(positionCurrent.x - positionLast.x, positionCurrent.y - positionLast.y)
        distanceLast = sqrt(positionDeltaLast.x.toDouble().pow(2.0) + positionDeltaLast.y.toDouble().pow(2.0))

        // delta and distance between -positionCurrent and -positionInitial
        positionDeltaInitial = PointF(positionCurrent.x - positionInitial.x, positionCurrent.y - positionInitial.y)
        distanceInitial = sqrt(positionDeltaInitial.x.toDouble().pow(2.0) + positionDeltaInitial.y.toDouble().pow(2.0))

        // duration
        durationLast = timeCurrent - timeLast.toDouble()
        durationInitial = timeCurrent - timeInitial.toDouble()
    }

    override fun toString(): String = "Finger(timeCurrent=$timeCurrent, positionCurrent=$positionCurrent, currentState=$stateCurrent)"

    companion object {
        // public default constants
        const val MIN_DISTANCE_SWIPE = 10
        const val MAX_DURATION_SWIPE = 400
        const val MIN_DISTANCE_MOVE = 30
        const val MAX_DURATION_DOUBLE_TAP = 250
        const val MAX_DOWN_DOUBLE_TAP = 100
        const val SLOPE_INTOLERANCE = 1
    }
}