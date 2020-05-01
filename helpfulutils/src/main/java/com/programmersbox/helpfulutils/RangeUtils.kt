package com.programmersbox.helpfulutils

/**
 * Use this if you want to have a number than must remain within a range that will
 * loop around if [loop] is true
 * remain at the range ends if [loop] is false
 */
class NumberRange(range: IntProgression, loop: Boolean = true) : ItemRange<Int>(*range.toList().toTypedArray(), loop = loop) {
    private val step = range.step
    operator fun plusAssign(n: Int) = run { current += n }
    operator fun minusAssign(n: Int) = run { current -= n }
    override operator fun inc() = apply { current += step }
    override operator fun dec() = apply { current -= step }
}

/**
 * Use this when you want to stay within a set of items. You can add or remove items from the list
 * loop around if [loop] is true
 * remain at the range ends if [loop] is false
 */
open class ItemRange<T>(vararg items: T, var loop: Boolean = true) {
    val itemList = items.toMutableList()
    val previousItem get() = itemList[previous]
    val item get() = itemList[current]
    val nextItem get() = itemList[next]
    var current = 0
        protected set(value) {
            field = when {
                itemList.isEmpty() -> throw IndexOutOfBoundsException("The list is empty")
                value > itemList.lastIndex -> if (loop) 0 else itemList.lastIndex
                value < 0 -> if (loop) itemList.lastIndex else 0
                else -> value
            }
        }
    val next
        get() = when {
            itemList.isEmpty() -> throw IndexOutOfBoundsException("The list is empty")
            current + 1 > itemList.lastIndex -> if (loop) 0 else itemList.lastIndex
            current + 1 < 0 -> if (loop) itemList.lastIndex else 0
            else -> current + 1
        }
    val previous
        get() = when {
            itemList.isEmpty() -> throw IndexOutOfBoundsException("The list is empty")
            current - 1 > itemList.lastIndex -> if (loop) 0 else itemList.lastIndex
            current - 1 < 0 -> if (loop) itemList.lastIndex else 0
            else -> current - 1
        }

    open operator fun inc() = apply { current += 1 }
    open operator fun dec() = apply { current -= 1 }
    operator fun iterator() = itemList.iterator()
    operator fun invoke() = item
}