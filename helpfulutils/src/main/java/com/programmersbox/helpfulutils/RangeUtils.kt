package com.programmersbox.helpfulutils

/**
 * Another way of doing this..that
 */
infix fun Int.through(that: Int): IntProgression = this..that

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
    /**
     * The list of items
     */
    val itemList = items.toMutableList()

    /**
     * Previous item
     */
    val previousItem get() = itemList[previous]

    /**
     * Current item
     */
    val item get() = itemList[current]

    /**
     * Next item
     */
    val nextItem get() = itemList[next]

    /**
     * Current index
     */
    var current = 0
        set(value) {
            field = when {
                itemList.isEmpty() -> throw IndexOutOfBoundsException("The list is empty")
                value > itemList.lastIndex -> if (loop) 0 else itemList.lastIndex
                value < 0 -> if (loop) itemList.lastIndex else 0
                else -> value
            }
            onChange(field, item)
        }

    /**
     * Next index
     */
    val next
        get() = when {
            itemList.isEmpty() -> throw IndexOutOfBoundsException("The list is empty")
            current + 1 > itemList.lastIndex -> if (loop) 0 else itemList.lastIndex
            current + 1 < 0 -> if (loop) itemList.lastIndex else 0
            else -> current + 1
        }

    /**
     * Previous index
     */
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
    operator fun get(index: Int) = itemList[index]
    protected open fun onChange(current: Int, item: T) = Unit
}

/**
 * Changes this [Iterable] to an [ItemRange]
 */
inline fun <reified T> Iterable<T>.toItemRange(loop: Boolean = true) = ItemRange(*toList().toTypedArray(), loop = loop)

/**
 * Changes this [String] to an [ItemRange]
 */
fun String.toItemRange(loop: Boolean = true) = toList().toItemRange(loop)
