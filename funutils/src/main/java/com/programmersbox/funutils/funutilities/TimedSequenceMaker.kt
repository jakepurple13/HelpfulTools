package com.programmersbox.funutils.funutilities

import android.os.CountDownTimer

class TimedSequenceMaker<T>(sequence: List<T>, private val timeout: Long = 5000, sequenceAchieved: () -> Unit) :
    SequenceMaker<T>(sequence, sequenceAchieved) {
    constructor(vararg sequence: T, timeout: Long = 5000, sequenceAchieved: () -> Unit) : this(sequence.toList(), timeout, sequenceAchieved)

    private val timeoutTimer: CountDownTimer? = if (timeout <= 0) null else object : CountDownTimer(timeout, 1000) {
        override fun onTick(millisUntilFinished: Long) = Unit
        override fun onFinish(): Unit = resetSequence().also { sequenceFailed() }
    }

    override fun nextItem(item: T) = timeoutTimer?.start().let { Unit }
    override fun add(item: T) {
        timeoutTimer?.cancel()
        super.add(item)
    }
}