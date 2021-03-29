package com.programmersbox.flowutils

import android.view.View
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.flow.*

/**
 * Creates a timer via flow
 */
@ObsoleteCoroutinesApi
fun timerFlow(delayMillis: Long, startInMs: Long = delayMillis, action: suspend () -> Unit): ReceiveChannel<Unit> =
    ticker(delayMillis, startInMs).apply { GlobalScope.launch { for (event in this@apply) action() } }

/**
 * Collect on the ui thread
 */
fun <T> Flow<T>.collectOnUi(scope: CoroutineScope = GlobalScope, action: (value: T) -> Unit) =
    scope.launch { collect { scope.launch(Dispatchers.Main) { action(it) } } }

/**
 * [combine]s this to [flow] as a Pair
 */
fun <T, R> Flow<T>.with(flow: Flow<R>) = combine(flow) { t, f -> t to f }

/**
 * collect from the flow on the ui loop
 */
fun <T> FlowItem<T>.collectOnUI(scope: CoroutineScope = GlobalScope, action: (value: T) -> Unit) =
    collect { scope.launch(Dispatchers.Main) { action(it) } }

/**
 * Bind the flow to a view
 */
fun <T, R : View> FlowItem<T>.bindToUI(view: R, action: R.(T) -> Unit) = collect { view.post { view.action(it) } }

//----------------------------------------------------------------------------------------------------------------------------------------------------

/**
 * Putting together [Flow.onEach] and [Flow.buffer] in that other
 */
fun <T> Flow<T>.onEachBuffer(
    capacity: Int = Channel.BUFFERED, onBufferOverflow: BufferOverflow = BufferOverflow.SUSPEND, action: suspend (T) -> Unit
) = onEach(action).buffer(capacity, onBufferOverflow)

/**
 * [Flow.combine] [other] and [Flow.filter] them with [predicate] before [Flow.map] the first
 */
fun <T, R> Flow<T>.withFilter(other: Flow<R>, predicate: (t: T, r: R) -> Boolean) = combine(other) { i, j -> i to j }
    .filter { predicate(it.first, it.second) }.map { it.first }

/**
 * @see withFilter except that the predicate is whatever [other] is
 */
fun <T> Flow<T>.withBooleanFilter(other: Flow<Boolean>) = combine(other) { i, j -> i to j }
    .filter { it.second }.map { it.first }

fun <T> T.asStateFlow() = MutableStateFlow(this)

fun <T> MutableStateFlow<T>.now() = run { value = value }

operator fun <T> MutableStateFlow<T>.invoke(item: T) = run { value = item }

operator fun <T> StateFlow<T>.invoke() = value

val <T : Collection<*>> StateFlow<T>.size get() = value.size

fun <T, R : MutableCollection<T>> MutableStateFlow<R>.add(item: T) = run { value = value.apply { add(item) } }

operator fun <T, R : Iterable<T>> StateFlow<R>.iterator() = value.iterator()
operator fun <T, R : Iterator<T>> StateFlow<R>.next() = value.next()
operator fun <T, R : Iterator<T>> StateFlow<R>.hasNext() = value.hasNext()
operator fun <T, R : Iterable<T>> StateFlow<R>.contains(other: T) = other in value
operator fun <T, R : List<T>> StateFlow<R>.get(index: Int) = value[index]
operator fun <T, R : MutableList<T>> MutableStateFlow<R>.set(index: Int, item: T) = run { value = value.apply { this[index] = item } }
operator fun <T, R : MutableList<T>> MutableStateFlow<R>.plusAssign(item: T) = add(item)

inline operator fun <reified T : Number> MutableStateFlow<T>.plusAssign(other: T) = when (value) {
    is Int -> invoke().toInt() + other.toInt()
    is Float -> invoke().toFloat() + other.toFloat()
    is Long -> invoke().toLong() + other.toLong()
    is Double -> invoke().toDouble() + other.toDouble()
    is Short -> (invoke().toShort() + other.toShort()).toShort()
    is Byte -> invoke().toByte() + other.toByte()
    else -> invoke()
}.let { value = it as T }

inline operator fun <reified T : Number> MutableStateFlow<T>.timesAssign(other: T) = when (value) {
    is Int -> invoke().toInt() * other.toInt()
    is Float -> invoke().toFloat() * other.toFloat()
    is Long -> invoke().toLong() * other.toLong()
    is Double -> invoke().toDouble() * other.toDouble()
    is Short -> (invoke().toShort() * other.toShort()).toShort()
    is Byte -> invoke().toByte() * other.toByte()
    else -> invoke()
}.let { value = it as T }

inline operator fun <reified T : Number> MutableStateFlow<T>.minusAssign(other: T) = when (value) {
    is Int -> invoke().toInt() - other.toInt()
    is Float -> invoke().toFloat() - other.toFloat()
    is Long -> invoke().toLong() - other.toLong()
    is Double -> invoke().toDouble() - other.toDouble()
    is Short -> (invoke().toShort() - other.toShort()).toShort()
    is Byte -> invoke().toByte() - other.toByte()
    else -> invoke()
}.let { value = it as T }

inline operator fun <reified T : Number> MutableStateFlow<T>.divAssign(other: T) = when (value) {
    is Int -> invoke().toInt() / other.toInt()
    is Float -> invoke().toFloat() / other.toFloat()
    is Long -> invoke().toLong() / other.toLong()
    is Double -> invoke().toDouble() / other.toDouble()
    is Short -> (invoke().toShort() / other.toShort()).toShort()
    is Byte -> invoke().toByte() / other.toByte()
    else -> invoke()
}.let { value = it as T }

inline operator fun <reified T : Number> MutableStateFlow<T>.remAssign(other: T) = when (value) {
    is Int -> invoke().toInt() % other.toInt()
    is Float -> invoke().toFloat() % other.toFloat()
    is Long -> invoke().toLong() % other.toLong()
    is Double -> invoke().toDouble() % other.toDouble()
    is Short -> (invoke().toShort() % other.toShort()).toShort()
    is Byte -> invoke().toByte() % other.toByte()
    else -> invoke()
}.let { value = it as T }