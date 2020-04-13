package com.programmersbox.funutils.funutilities

open class SequenceMaker<T>(private val sequence: List<T>, private val sequenceAchieved: () -> Unit) {
    constructor(vararg sequence: T, sequenceAchieved: () -> Unit) : this(sequence.toList(), sequenceAchieved)

    protected var sequenceFailed: () -> Unit = {}
    private val currentSequence = mutableListOf<T>()
    fun sequenceReset(block: () -> Unit) = apply { sequenceFailed = block }
    fun resetSequence() = currentSequence.clear()
    private fun validateSequence() = currentSequence.lastIndex.let { currentSequence[it] == sequence[it] }
    private fun isAchieved() = currentSequence == sequence
    protected open fun nextItem(item: T) = Unit
    fun currentSequence() = currentSequence.toList()
    fun correctSequence() = sequence.toList()
    operator fun plusAssign(order: T) = add(order)
    operator fun plusAssign(list: Iterable<T>) = add(list)
    operator fun plusAssign(items: Array<T>) = add(*items)
    fun add(list: Iterable<T>) = list.forEach(::add)
    fun add(vararg items: T) = items.forEach(::add)
    open fun add(item: T) = addItem(item)
    private fun addItem(item: T) {
        currentSequence += item
        if (validateSequence()) {
            if (isAchieved()) {
                sequenceAchieved()
                resetSequence()
            } else nextItem(item)
        } else resetSequence().also { sequenceFailed() }
    }
}