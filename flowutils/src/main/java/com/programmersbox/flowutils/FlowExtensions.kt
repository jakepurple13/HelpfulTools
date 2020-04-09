package com.programmersbox.flowutils

import android.view.View
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.flow.Flow
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
fun <T> Flow<T>.collectOnUi(action: (T) -> Unit) = GlobalScope.launch { collect { GlobalScope.launch(Dispatchers.Main) { action(it) } } }

/**
 * collect from the flow on the ui loop
 */
fun <T> FlowItem<T>.collectOnUI(action: (value: T) -> Unit) = collect { GlobalScope.launch(Dispatchers.Main) { action(it) } }

/**
 * Bind the flow to a view
 */
fun <T, R : View> FlowItem<T>.bindToUI(view: R, action: R.(T) -> Unit) = collect { view.post { view.action(it) } }
