package com.programmersbox.funutils.funutilities

import androidx.annotation.CallSuper

open class SequenceMaker<T>(private val sequence: List<T>, protected var sequenceListener: SequenceListener? = null) {
    constructor(vararg sequence: T, sequenceListener: SequenceListener? = null) : this(sequence.toList(), sequenceListener)

    private val currentSequence = mutableListOf<T>()
    fun setListener(listener: SequenceListener?) = apply { sequenceListener = listener }
    fun resetSequence() = currentSequence.clear().also { sequenceListener?.onReset() }
    private fun validateSequence() = currentSequence.lastIndex.let { currentSequence[it] == sequence[it] }
    private fun isAchieved() = currentSequence == sequence
    protected open fun nextItem(item: T) = Unit
    fun currentSequence() = currentSequence.toList()
    fun correctSequence() = sequence.toList()
    operator fun plusAssign(order: T) = add(order)
    operator fun plusAssign(list: Iterable<T>) = add(list)
    operator fun plusAssign(items: Array<T>) = add(*items)
    operator fun contains(item: T) = item in currentSequence
    fun add(list: Iterable<T>) = list.forEach(::addNewItem)
    fun add(vararg items: T) = items.forEach(::addNewItem)

    @CallSuper
    protected open fun internalAchieved() {
        sequenceListener?.onAchieved()
    }

    @CallSuper
    open fun addNewItem(item: T) = addItem(item)

    private fun addItem(item: T) {
        currentSequence += item
        if (validateSequence()) {
            nextItem(item)
            if (isAchieved()) {
                internalAchieved()
                resetSequence()
            }
        } else sequenceListener?.onFail().also { resetSequence() }
    }
}

fun interface SequenceListener {
    fun onAchieved()
    fun onFail() {}
    fun onReset() {}
}
