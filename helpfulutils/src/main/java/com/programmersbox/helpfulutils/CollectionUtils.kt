package com.programmersbox.helpfulutils

import android.os.Build
import androidx.annotation.RequiresApi
import kotlin.random.Random

/**
 * a vararg version of [MutableList.addAll]
 */
fun <T> MutableCollection<T>.addAll(vararg args: T) = addAll(args)

/**
 * Finds similarities between two lists based on a predicate
 */
fun <T, R> Iterable<T>.intersect(uList: Iterable<R>, filterPredicate: (T, R) -> Boolean) = filter { m -> uList.any { filterPredicate(m, it) } }

/**
 * Another way to call the [Iterable.intersect] method
 * @see intersect
 */
fun <T, R> Pair<Iterable<T>, Iterable<R>>.intersect(predicate: (T, R) -> Boolean) = first.intersect(second, predicate)

/**
 * randomly removes one element
 */
fun <T> MutableList<T>.randomRemove(): T = removeAt(Random.nextInt(size))

/**
 * removes a random element based on a [predicate]
 * @throws NoSuchElementException if no elements match the [predicate]
 */
fun <T> MutableList<T>.randomRemove(predicate: (T) -> Boolean): T = removeAt(indexOf(filter(predicate).random()))

/**
 * get a random element based on a [predicate]
 * @throws NoSuchElementException if no elements match the [predicate]
 */
fun <T> Iterable<T>.random(predicate: (T) -> Boolean) = filter(predicate).random()

/**
 * Creates a list of [amount] size
 * Useful for random information
 */
fun <T> sizedListOf(amount: Int = 1, item: (Int) -> T) = mutableListOf<T>().apply { repeat(amount) { this += item(it) } }

/**
 * pairs [List.lastIndex] with [List.last]
 */
val <T> List<T>.lastWithIndex: Pair<Int, T> get() = lastIndex to last()

fun <T> Iterable<T>.toFixedList(size: Int) = FixedList(size, c = toMutableList())
fun <T> Iterable<T>.toFixedSet(size: Int) = FixedSet(size, c = toMutableSet())

enum class FixedListLocation {
    /**
     * Remove from the start
     * AKA 0
     */
    START,

    /**
     * Remove from the end
     * AKA [List.lastIndex]
     */
    END
}

fun <T> fixedListOf(size: Int, vararg elements: T): FixedList<T> = FixedList(size, c = elements.toMutableList())

open class FixedList<T> : ArrayList<T> {

    /**
     * set the max size this list can hold
     */
    var fixedSize: Int
        set(value) {
            field = value
            if (size > value) removeRange(value, size)
        }

    /**
     * choose where to remove items from
     * Default is [FixedListLocation.END]
     */
    var removeFrom: FixedListLocation = FixedListLocation.END

    constructor(fixedSize: Int, location: FixedListLocation = FixedListLocation.END) : super() {
        this.fixedSize = fixedSize
        this.removeFrom = location
    }

    constructor(fixedSize: Int, location: FixedListLocation = FixedListLocation.END, c: MutableCollection<out T>) : super(c) {
        this.fixedSize = fixedSize
        this.removeFrom = location
    }

    constructor(fixedSize: Int, location: FixedListLocation = FixedListLocation.END, initialCapacity: Int) : super(initialCapacity) {
        this.fixedSize = fixedSize
        this.removeFrom = location
    }

    private val removeIndex
        get() = when (removeFrom) {
            FixedListLocation.START -> 0
            FixedListLocation.END -> lastIndex
        }

    protected fun singleSizeCheck() {
        if (size > fixedSize) removeAt(removeIndex)
    }

    override fun add(element: T): Boolean {
        val addition = super.add(element)
        singleSizeCheck()
        return addition
    }

    override fun add(index: Int, element: T) {
        val addition = super.add(index, element)
        singleSizeCheck()
        return addition
    }

    protected fun multipleSizeCheck() {
        while (size > fixedSize) removeAt(removeIndex)
    }

    override fun addAll(elements: Collection<T>): Boolean {
        val addition = super.addAll(elements)
        multipleSizeCheck()
        return addition
    }

    override fun addAll(index: Int, elements: Collection<T>): Boolean {
        val addition = super.addAll(index, elements)
        multipleSizeCheck()
        return addition
    }

}

fun <K, V> fixedMapOf(size: Int, vararg elements: Pair<K, V>): FixedMap<K, V> = FixedMap<K, V>(size).apply { putAll(elements) }

open class FixedMap<K, V> : LinkedHashMap<K, V> {

    constructor(fixedSize: Int, location: FixedListLocation = FixedListLocation.END) : super() {
        this.fixedSize = fixedSize
        this.removeFrom = location
    }

    constructor(fixedSize: Int, location: FixedListLocation = FixedListLocation.END, initialCapacity: Int) : super(initialCapacity) {
        this.fixedSize = fixedSize
        this.removeFrom = location
    }

    constructor(fixedSize: Int, location: FixedListLocation = FixedListLocation.END, initialCapacity: Int, loadFactor: Float) : super(
        initialCapacity,
        loadFactor
    ) {
        this.fixedSize = fixedSize
        this.removeFrom = location
    }

    constructor(fixedSize: Int, location: FixedListLocation = FixedListLocation.END, c: MutableMap<out K, out V>?) : super(c) {
        this.fixedSize = fixedSize
        this.removeFrom = location
    }

    constructor(
        fixedSize: Int,
        location: FixedListLocation = FixedListLocation.END,
        initialCapacity: Int,
        loadFactor: Float,
        accessOrder: Boolean
    ) : super(initialCapacity, loadFactor, accessOrder) {
        this.fixedSize = fixedSize
        this.removeFrom = location
    }

    /**
     * set the max size this list can hold
     */
    var fixedSize: Int = 0
        set(value) {
            field = value
            multipleSizeCheck()
        }

    /**
     * choose where to remove items from
     * Default is [FixedListLocation.END]
     */
    var removeFrom: FixedListLocation = FixedListLocation.END

    private val removeIndex
        get() = when (removeFrom) {
            FixedListLocation.START -> keys.firstOrNull()
            FixedListLocation.END -> keys.lastOrNull()
        }

    protected fun singleSizeCheck() {
        if (size > fixedSize) remove(removeIndex)
    }

    protected fun multipleSizeCheck() {
        while (size > fixedSize) remove(removeIndex)
    }

    override fun put(key: K, value: V): V? {
        val putting = super.put(key, value)
        singleSizeCheck()
        return putting
    }

    override fun putAll(from: Map<out K, V>) {
        super.putAll(from)
        multipleSizeCheck()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun putIfAbsent(key: K, value: V): V? {
        val putting = super.putIfAbsent(key, value)
        singleSizeCheck()
        return putting
    }

}

fun <T> fixedSetOf(size: Int, vararg elements: T): FixedSet<T> = FixedSet(size, c = elements.toMutableSet())

open class FixedSet<T> : LinkedHashSet<T> {

    constructor(fixedSize: Int, location: FixedListLocation = FixedListLocation.END) : super() {
        this.fixedSize = fixedSize
        this.removeFrom = location
    }

    constructor(fixedSize: Int, location: FixedListLocation = FixedListLocation.END, initialCapacity: Int) : super(initialCapacity) {
        this.fixedSize = fixedSize
        this.removeFrom = location
    }

    constructor(fixedSize: Int, location: FixedListLocation = FixedListLocation.END, c: MutableCollection<out T>) : super(c) {
        this.fixedSize = fixedSize
        this.removeFrom = location
    }

    constructor(fixedSize: Int, location: FixedListLocation = FixedListLocation.END, initialCapacity: Int, loadFactor: Float) : super(
        initialCapacity,
        loadFactor
    ) {
        this.fixedSize = fixedSize
        this.removeFrom = location
    }

    /**
     * set the max size this list can hold
     */
    var fixedSize: Int = 0
        set(value) {
            field = value
            multipleSizeCheck()
        }

    /**
     * choose where to remove items from
     * Default is [FixedListLocation.END]
     */
    var removeFrom: FixedListLocation = FixedListLocation.END

    private val removeIndex
        get() = when (removeFrom) {
            FixedListLocation.START -> firstOrNull()
            FixedListLocation.END -> lastOrNull()
        }

    protected fun singleSizeCheck() {
        if (size > fixedSize) remove(removeIndex)
    }

    protected fun multipleSizeCheck() {
        while (size > fixedSize) remove(removeIndex)
    }

    override fun add(element: T): Boolean {
        val adding = super.add(element)
        singleSizeCheck()
        return adding
    }

    override fun addAll(elements: Collection<T>): Boolean {
        val adding = super.addAll(elements)
        multipleSizeCheck()
        return adding
    }

}