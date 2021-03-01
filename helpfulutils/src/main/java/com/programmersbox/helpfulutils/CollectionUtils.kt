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
 * randomly removes [n] items
 */
fun <T> MutableList<T>.randomNRemove(n: Int): List<T> = sizedListOf(n) { randomRemove() }

/**
 * randomly creates a list with [n] items
 */
fun <T> MutableList<T>.randomN(n: Int): List<T> = sizedListOf(n) { random() }

/**
 * If you want to group a list by a condition
 */
fun <T, R> Iterable<T>.groupByCondition(key: (T) -> R, predicate: (key: T, element: T) -> Boolean): Map<R, List<T>> =
    map { name -> key(name) to filter { s -> predicate(name, s) } }.distinctBy(Pair<R, List<T>>::second).toMap()

/**
 * If you want to group a sequence by a condition
 */
fun <T, R> Sequence<T>.groupByCondition(key: (T) -> R, predicate: (key: T, element: T) -> Boolean) =
    map { name -> key(name) to filter { s -> predicate(name, s) } }.distinctBy { it.second.toList() }.map { it.first to it.second.toList() }

/**
 * A way to fold everything starting with the first element
 * @throws IndexOutOfBoundsException if there is no elements in [Iterable]
 */
fun <T, R> Iterable<T>.foldEverything(map: T.() -> R, operation: (acc: R, T) -> R) = drop(1).fold(first().map(), operation)

/**
 * An easy way to use [List.subList]
 */
operator fun <T> List<T>.get(range: IntRange) = subList(range.first, range.last)

/**
 * checks is there is no instance of [T]
 */
inline fun <reified T> Iterable<*>.noneIsInstance() = none { it is T }

/**
 * checks is there is any instance of [T]
 */
inline fun <reified T> Iterable<*>.anyIsInstance() = any { it is T }

/**
 * checks is there is only instances of [T]
 */
inline fun <reified T> Iterable<*>.allIsInstance() = all { it is T }

/**
 * counts all the instances of [T]
 */
inline fun <reified T> Iterable<*>.countInstance() = count { it is T }

/**
 * A way to fill up all lists to make sure they all have the same size
 */
infix fun <T> Iterable<Iterable<T>>.fillWith(defaultValue: T): List<List<T>> = maxByOrNull(Iterable<*>::count)
    ?.count()
    ?.let { maxSize -> map { it.toMutableList().apply { while (size < maxSize) add(defaultValue) } } }
    .orEmpty()

/**
 * checks to see if the [Iterable] contains any duplicates
 */
fun <T> Iterable<T>.containsDuplicates(predicate: (i: T, j: T) -> Boolean = { i, j -> i == j }): Boolean {
    for (i in this.withIndex()) {
        for (j in this.withIndex()) {
            if (i.index == j.index) continue
            if (predicate(i.value, j.value)) return true
        }
    }
    return false
}

/**
 * An easy way to map an [Iterable] to a [Map]
 */
fun <T, R, Y> Iterable<T>.toMap(pair: (T) -> Pair<Y, R>) = map(pair).toMap()

/**
 * Creates a list of [amount] size
 * Useful for random information
 */
fun <T> sizedListOf(amount: Int = 1, item: (Int) -> T): List<T> = mutableListOf<T>().apply { repeat(amount) { this += item(it) } }

/**
 * Creates a map of [amount] size
 * Useful for random information
 */
fun <T, R> sizedMapOf(amount: Int = 1, item: (Int) -> Pair<T, R>): Map<T, R> = mutableMapOf<T, R>().apply { repeat(amount) { this += item(it) } }

/**
 * Creates set of [amount] size
 * Useful for random information
 */
fun <T> sizedSetOf(amount: Int = 1, item: (Int) -> T): Set<T> = mutableSetOf<T>().apply { repeat(amount) { this += item(it) } }

/**
 * pairs [List.lastIndex] with [List.last]
 */
val <T> List<T>.lastWithIndex: Pair<Int, T> get() = lastIndex to last()

/**
 * Remove an element from [MutableList] at [location]
 */
infix fun <T> MutableList<T>.removeFrom(location: FixedListLocation) = removeAt(
    when (location) {
        FixedListLocation.START -> 0
        FixedListLocation.END -> lastIndex
    }
)

/**
 * Remove an element from [list] at [FixedListLocation] location
 */
infix fun <T> FixedListLocation.removeFrom(list: MutableList<T>) = list.removeAt(
    when (this) {
        FixedListLocation.START -> 0
        FixedListLocation.END -> list.lastIndex
    }
)

/**
 * Returns an empty new [FixedList]
 */
fun <T> Iterable<T>.toFixedList(size: Int) = FixedList(size, c = toMutableList())

/**
 * Returns an empty new [FixedSet]
 */
fun <T> Iterable<T>.toFixedSet(size: Int) = FixedSet(size, c = toMutableSet())

/**
 * Returns an empty new [FixedMap]
 */
fun <K, V> Map<out K, V>.toFixedMap(size: Int) = FixedMap(size, c = toMutableMap())

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

fun <T> fixedListOf(size: Int, vararg elements: T): FixedList<T> = FixedList(size, c = elements.toList())

open class FixedList<T> : ArrayList<T> {

    /**
     * set the max size this list can hold
     */
    var fixedSize: Int
        set(value) {
            require(value > 0) { "FixedSize must be greater than 0" }
            field = value
            multipleSizeCheck()
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

    constructor(fixedSize: Int, location: FixedListLocation = FixedListLocation.END, c: Collection<T>) : super(c.toMutableList()) {
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

    override fun toString(): String = "FixedSize=$fixedSize, RemoveFrom=$removeFrom, ${super.toString()}"

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

    constructor(fixedSize: Int, location: FixedListLocation = FixedListLocation.END, c: Map<out K, V>?) : super(c?.toMutableMap()) {
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
            require(value > 0) { "FixedSize must be greater than 0" }
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

    override fun toString(): String = "FixedSize=$fixedSize, RemoveFrom=$removeFrom, ${super.toString()}"

}

fun <T> fixedSetOf(size: Int, vararg elements: T): FixedSet<T> = FixedSet(size, c = elements.toMutableSet())

open class FixedSet<T> : HashSet<T> {

    constructor(fixedSize: Int, location: FixedListLocation = FixedListLocation.END) : super() {
        this.fixedSize = fixedSize
        this.removeFrom = location
    }

    constructor(fixedSize: Int, location: FixedListLocation = FixedListLocation.END, initialCapacity: Int) : super(initialCapacity) {
        this.fixedSize = fixedSize
        this.removeFrom = location
    }

    constructor(fixedSize: Int, location: FixedListLocation = FixedListLocation.END, c: Collection<T>) : super(c.toMutableSet()) {
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
    var fixedSize: Int
        set(value) {
            require(value > 0) { "FixedSize must be greater than 0" }
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
        if (size > fixedSize && fixedSize != 0) remove(removeIndex)
    }

    protected fun multipleSizeCheck() {
        while (size > fixedSize && fixedSize != 0) remove(removeIndex)
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

    override fun toString(): String = "FixedSize=$fixedSize, RemoveFrom=$removeFrom, ${super.toString()}"

}