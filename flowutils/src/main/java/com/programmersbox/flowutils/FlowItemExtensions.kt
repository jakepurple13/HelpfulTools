package com.programmersbox.flowutils

inline fun <reified T, R> combine(vararg flowItems: FlowItem<T>, crossinline transform: suspend (Array<T>) -> R) =
    kotlinx.coroutines.flow.combine(flowItems.map(FlowItem<T>::flow), transform)

val <T : Collection<*>> FlowItem<T>.size get() = getValue().size

fun <T, R : MutableCollection<T>> FlowItem<R>.add(item: T) = getValue().add(item).also { now() }

operator fun <T, R : Iterable<T>> FlowItem<R>.iterator() = getValue().iterator()
operator fun <T, R : Iterator<T>> FlowItem<R>.next() = getValue().next()
operator fun <T, R : Iterator<T>> FlowItem<R>.hasNext() = getValue().hasNext()
operator fun <T, R : Iterable<T>> FlowItem<R>.contains(other: T) = other in getValue()
operator fun <T, R : List<T>> FlowItem<R>.get(index: Int) = getValue()[index]
operator fun <T, R : MutableList<T>> FlowItem<R>.set(index: Int, item: T) = setValue(getValue().apply { this[index] = item })
operator fun <T, R : MutableList<T>> FlowItem<R>.plusAssign(item: T) = setValue(getValue().apply { add(item) })

operator fun FlowItem<Boolean>.not() = setValue(!invoke())

inline operator fun <reified T : Number> FlowItem<T>.plusAssign(other: T) = when (getValue()) {
    is Int -> invoke().toInt() + other.toInt()
    is Float -> invoke().toFloat() + other.toFloat()
    is Long -> invoke().toLong() + other.toLong()
    is Double -> invoke().toDouble() + other.toDouble()
    is Short -> (invoke().toShort() + other.toShort()).toShort()
    is Byte -> invoke().toByte() + other.toByte()
    else -> invoke()
}.let { setValue(it as T) }

inline operator fun <reified T : Number> FlowItem<T>.timesAssign(other: T) = when (getValue()) {
    is Int -> invoke().toInt() * other.toInt()
    is Float -> invoke().toFloat() * other.toFloat()
    is Long -> invoke().toLong() * other.toLong()
    is Double -> invoke().toDouble() * other.toDouble()
    is Short -> (invoke().toShort() * other.toShort()).toShort()
    is Byte -> invoke().toByte() * other.toByte()
    else -> invoke()
}.let { setValue(it as T) }

inline operator fun <reified T : Number> FlowItem<T>.minusAssign(other: T) = when (getValue()) {
    is Int -> invoke().toInt() - other.toInt()
    is Float -> invoke().toFloat() - other.toFloat()
    is Long -> invoke().toLong() - other.toLong()
    is Double -> invoke().toDouble() - other.toDouble()
    is Short -> (invoke().toShort() - other.toShort()).toShort()
    is Byte -> invoke().toByte() - other.toByte()
    else -> invoke()
}.let { setValue(it as T) }

inline operator fun <reified T : Number> FlowItem<T>.divAssign(other: T) = when (getValue()) {
    is Int -> invoke().toInt() / other.toInt()
    is Float -> invoke().toFloat() / other.toFloat()
    is Long -> invoke().toLong() / other.toLong()
    is Double -> invoke().toDouble() / other.toDouble()
    is Short -> (invoke().toShort() / other.toShort()).toShort()
    is Byte -> invoke().toByte() / other.toByte()
    else -> invoke()
}.let { setValue(it as T) }

inline operator fun <reified T : Number> FlowItem<T>.remAssign(other: T) = when (getValue()) {
    is Int -> invoke().toInt() % other.toInt()
    is Float -> invoke().toFloat() % other.toFloat()
    is Long -> invoke().toLong() % other.toLong()
    is Double -> invoke().toDouble() % other.toDouble()
    is Short -> (invoke().toShort() % other.toShort()).toShort()
    is Byte -> invoke().toByte() % other.toByte()
    else -> invoke()
}.let { setValue(it as T) }