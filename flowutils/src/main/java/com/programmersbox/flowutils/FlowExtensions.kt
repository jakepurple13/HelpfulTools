package com.programmersbox.flowutils

import android.view.View
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * Creates a timer via flow
 */
@ObsoleteCoroutinesApi
fun timerFlow(delayMillis: Long, startInMs: Long = delayMillis, action: suspend () -> Unit): ReceiveChannel<Unit> =
    ticker(delayMillis, startInMs).apply { GlobalScope.launch { for (event in this@apply) action() } }

/**
 * Collect on the ui thread
 */
fun <T> Flow<T>.collectOnUi(action: (value: T) -> Unit) = GlobalScope.launch { collect { GlobalScope.launch(Dispatchers.Main) { action(it) } } }

/**
 * collect from the flow on the ui loop
 */
fun <T> FlowItem<T>.collectOnUI(action: (value: T) -> Unit) = collect { GlobalScope.launch(Dispatchers.Main) { action(it) } }

/**
 * Bind the flow to a view
 */
fun <T, R : View> FlowItem<T>.bindToUI(view: R, action: R.(T) -> Unit) = collect { view.post { view.action(it) } }

//----------------------------------------------------------------------------------------------------------------------------------------------------

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