package com.programmersbox.funutils.funutilities

import android.os.CountDownTimer

open class TimedSequenceMaker<T>(sequence: List<T>, private val timeout: Long = 5000, sequenceListener: SequenceListener?) :
    SequenceMaker<T>(sequence, sequenceListener) {
    constructor(vararg sequence: T, timeout: Long = 5000, sequenceListener: SequenceListener?) : this(sequence.toList(), timeout, sequenceListener)

    private val timeoutTimer: CountDownTimer? = if (timeout <= 0) null else object : CountDownTimer(timeout, 1000) {
        override fun onTick(millisUntilFinished: Long) = Unit
        override fun onFinish(): Unit = resetSequence().also { this@TimedSequenceMaker.sequenceListener?.onFail() }
    }

    override fun nextItem(item: T) = timeoutTimer?.start().let { }
    override fun addNewItem(item: T) {
        timeoutTimer?.cancel()
        super.addNewItem(item)
    }
}