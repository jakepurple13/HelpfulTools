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
class NumberRange(override val itemList: List<Int>, val step: Int) : Range<Int>() {
    constructor(range: IntProgression) : this(range.toList(), range.step)

    override operator fun inc() = apply { current += step }
    override operator fun dec() = apply { current -= step }
    override fun onChange(current: Int, item: Int) = Unit

    override fun toString(): String = "Step: $step, ${super.toString()}"
}

/**
 * Use this when you want to stay within a set of items. You can add or remove items from the list
 * loop around if [loop] is true
 * remain at the range ends if [loop] is false
 */
class MutableItemRange<T>(override val itemList: MutableList<T>) : Range<T>(), MutableList<T> by itemList {
    constructor(vararg items: T) : this(items.toMutableList())

    override fun onChange(current: Int, item: T) = Unit
}

/**
 * Use this when you want to stay within a set of items. You can add or remove items from the list
 * loop around if [loop] is true
 * remain at the range ends if [loop] is false
 */
class ItemRange<T>(override val itemList: List<T>) : Range<T>(), List<T> by itemList {
    constructor(vararg items: T) : this(items.toList())

    override fun onChange(current: Int, item: T) = Unit
}

/**
 * Use this when you want to stay within a set of items.
 */
abstract class Range<T> {

    /**
     * The list of items
     */
    abstract val itemList: List<T>

    /**
     * loop around if [loop] is true
     * remain at the range ends if [loop] is false
     */
    var loop: Boolean = true

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

    /**
     * Call [onChange] for any actions that you want to call now
     */
    fun applyNow() = onChange(current, item)

    open operator fun inc() = apply { current += 1 }
    open operator fun dec() = apply { current -= 1 }
    open operator fun plusAssign(amount: Int) = run { current += amount }
    open operator fun minusAssign(amount: Int) = run { current -= amount }
    operator fun invoke() = item
    operator fun next() = run { current += 1 }
    operator fun hasNext() = current + 1 > itemList.lastIndex
    protected abstract fun onChange(current: Int, item: T)
    override fun toString(): String = "Loop: $loop, Current: $current, $itemList"

    fun toItemRange() = ItemRange(itemList).apply { this@apply.loop = this@Range.loop }
    fun toMutableItemRange() = MutableItemRange(itemList).apply { this@apply.loop = this@Range.loop }
}

fun Range<Int>.toNumberRange() = NumberRange(itemList, 1).apply { this.loop = this@toNumberRange.loop }

/**
 * Creates a [MutableItemRange] from [item]
 */
fun <T> mutableItemRangeOf(vararg item: T) = MutableItemRange(*item)

/**
 * Creates an [ItemRange] from [item]
 */
fun <T> itemRangeOf(vararg item: T) = ItemRange(*item)

/**
 * Changes this [Iterable] to an [ItemRange]
 */
inline fun <reified T> Iterable<T>.toItemRange() = ItemRange(*toList().toTypedArray())

/**
 * Changes this [String] to an [ItemRange]
 */
fun String.toItemRange() = toList().toItemRange()