package com.programmersbox.flowutils

import android.view.View
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

/**
 * Use this if you want to set an object up with flow easily
 */
class FlowItem<T>(startingValue: T, capacity: Int = 1) {
    private val itemBroadcast = BroadcastChannel<T>(capacity)
    private val itemFlow = itemBroadcast.asFlow().onStart { emit(flowItem) }
    /**
     * the flow
     */
    val flow get() = itemFlow
    private var flowItem: T = startingValue
        set(value) {
            field = value
            itemBroadcast.sendLaunch(value)
        }

    /**
     * collect from the flow
     */
    fun collect(action: suspend (value: T) -> Unit) = itemFlow.flowQuery(action)

    /**
     * collect from the flow on the ui loop
     */
    fun collectOnUI(action: (value: T) -> Unit) = itemFlow.flowQuery { GlobalScope.launch(Dispatchers.Main) { action(it) } }

    /**
     * Bind the flow to a view
     */
    fun <R : View> bindToUI(view: R, action: R.(T) -> Unit) = itemFlow.flowQuery { view.post { view.action(it) } }

    /**
     * calls [getValue]
     * @see getValue
     */
    operator fun invoke() = getValue()

    /**
     * calls [setValue]
     * @see setValue
     */
    operator fun invoke(value: T) = setValue(value)

    /**
     * get the current value
     */
    fun getValue() = flowItem

    /**
     * set the value
     */
    fun setValue(value: T) = run { flowItem = value }

    /**
     * set the flow to itself just to get a call
     */
    fun now() = setValue(flowItem)

    private fun <T> SendChannel<T>.sendLaunch(value: T) = GlobalScope.launch { send(value) }
    private fun <T> Flow<T>.flowQuery(block: suspend (T) -> Unit) = GlobalScope.launch { collect(block) }

    override fun toString(): String = "FlowItem(value=$flowItem)"
}

fun <T> T.asFlowItem() = FlowItem(this)

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