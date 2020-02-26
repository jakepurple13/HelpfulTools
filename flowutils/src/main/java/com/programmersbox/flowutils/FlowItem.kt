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
    private val itemFlow = itemBroadcast.asFlow().onStart { emit(startingValue) }
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
     * called [setValue]
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
}

fun <T> T.asFlowItem() = FlowItem(this)

operator fun FlowItem<Boolean>.not() = setValue(!invoke())

operator fun FlowItem<Int>.plusAssign(other: Int) = setValue(invoke() + other)
operator fun FlowItem<Float>.plusAssign(other: Float) = setValue(invoke() + other)
operator fun FlowItem<Double>.plusAssign(other: Double) = setValue(invoke() + other)
operator fun FlowItem<Long>.plusAssign(other: Long) = setValue(invoke() + other)
operator fun FlowItem<Short>.plusAssign(other: Short) = setValue((invoke() + other).toShort())

operator fun FlowItem<Int>.timesAssign(other: Int) = setValue(invoke() * other)
operator fun FlowItem<Float>.timesAssign(other: Float) = setValue(invoke() * other)
operator fun FlowItem<Double>.timesAssign(other: Double) = setValue(invoke() * other)
operator fun FlowItem<Long>.timesAssign(other: Long) = setValue(invoke() * other)
operator fun FlowItem<Short>.timesAssign(other: Short) = setValue((invoke() * other).toShort())

operator fun FlowItem<Int>.minusAssign(other: Int) = setValue(invoke() - other)
operator fun FlowItem<Float>.minusAssign(other: Float) = setValue(invoke() - other)
operator fun FlowItem<Double>.minusAssign(other: Double) = setValue(invoke() - other)
operator fun FlowItem<Long>.minusAssign(other: Long) = setValue(invoke() - other)
operator fun FlowItem<Short>.minusAssign(other: Short) = setValue((invoke() - other).toShort())

operator fun FlowItem<Int>.divAssign(other: Int) = setValue(invoke() / other)
operator fun FlowItem<Float>.divAssign(other: Float) = setValue(invoke() / other)
operator fun FlowItem<Double>.divAssign(other: Double) = setValue(invoke() / other)
operator fun FlowItem<Long>.divAssign(other: Long) = setValue(invoke() / other)
operator fun FlowItem<Short>.divAssign(other: Short) = setValue((invoke() / other).toShort())

operator fun FlowItem<Int>.remAssign(other: Int) = setValue(invoke() % other)
operator fun FlowItem<Float>.remAssign(other: Float) = setValue(invoke() % other)
operator fun FlowItem<Double>.remAssign(other: Double) = setValue(invoke() % other)
operator fun FlowItem<Long>.remAssign(other: Long) = setValue(invoke() % other)
operator fun FlowItem<Short>.remAssign(other: Short) = setValue((invoke() % other).toShort())