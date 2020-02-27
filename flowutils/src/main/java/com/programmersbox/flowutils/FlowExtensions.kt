package com.programmersbox.flowutils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * Creates a timer via flow
 */
fun timerFlow(delayMillis: Long, startInMs: Long = delayMillis, action: suspend () -> Unit): ReceiveChannel<Unit> =
    ticker(delayMillis, startInMs).apply { GlobalScope.launch { for (event in this@apply) action() } }

/**
 * Collect on the ui thread
 */
fun <T> Flow<T>.collectOnUi(action: (T) -> Unit) = GlobalScope.launch { collect { GlobalScope.launch(Dispatchers.Main) { action(it) } } }
