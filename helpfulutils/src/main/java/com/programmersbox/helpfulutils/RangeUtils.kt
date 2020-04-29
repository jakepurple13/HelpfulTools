package com.programmersbox.helpfulutils

/**
 * Use this if you want to have a number than must remain within a range that will
 * loop around if [loop] is true
 * remain at the range ends if [loop] is false
 */
class NumberRange(val range: IntRange, var loop: Boolean = true) {
    var current = range.first
        private set(value) {
            field = when {
                value > range.last -> if (loop) range.first else range.last
                value < range.first -> if (loop) range.last else range.first
                else -> value
            }
        }

    operator fun plusAssign(n: Int) = run { current += n }
    operator fun minusAssign(n: Int) = run { current -= n }
    operator fun inc() = apply { current += range.step }
    operator fun dec() = apply { current -= range.step }
    operator fun iterator() = range.iterator()
    operator fun invoke() = current
}

/**
 * Use this when you want to stay within a set of items. You can add or remove items from the list
 * loop around if [loop] is true
 * remain at the range ends if [loop] is false
 */
class ItemRange<T>(vararg items: T, var loop: Boolean = true) {
    val itemList = items.toMutableList()
    val item get() = itemList[current]
    var current = 0
        private set(value) {
            field = when {
                itemList.isEmpty() -> throw IndexOutOfBoundsException("The list is empty")
                value > itemList.lastIndex -> if (loop) 0 else itemList.lastIndex
                value < 0 -> if (loop) itemList.lastIndex else 0
                else -> value
            }
        }

    operator fun inc() = apply { current += 1 }
    operator fun dec() = apply { current -= 1 }
    operator fun iterator() = itemList.iterator()
    operator fun invoke() = item
}