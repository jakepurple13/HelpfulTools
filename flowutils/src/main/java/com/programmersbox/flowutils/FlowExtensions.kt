package com.programmersbox.flowutils

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.launch

/**
 * Creates a timer via flow
 */
fun timerFlow(delayMillis: Long, startInMs: Long = delayMillis, action: suspend () -> Unit): ReceiveChannel<Unit> =
    ticker(delayMillis, startInMs).apply { GlobalScope.launch { for (event in this@apply) action() } }