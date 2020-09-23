@file:Suppress("EXPERIMENTAL_API_USAGE")

package com.programmersbox.flowutils

import android.view.DragEvent
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.widget.CompoundButton
import android.widget.TextView
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doBeforeTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.callbackFlow

fun View.clicks(): Flow<Unit> = callbackFlow { setOnClickListener { offer(Unit) };awaitClose() }
fun View.longClicks(): Flow<Unit> = callbackFlow { setOnLongClickListener { offer(Unit);true };awaitClose() }
fun View.touches(): Flow<MotionEvent> = BroadcastChannel<MotionEvent>(1).apply { setOnTouchListener { _, event -> offer(event);true } }.asFlow()
fun View.drags(): Flow<DragEvent> = BroadcastChannel<DragEvent>(1).apply { setOnDragListener { _, event -> offer(event) } }.asFlow()
fun View.keyPress(): Flow<KeyEvent> = BroadcastChannel<KeyEvent>(1).apply { setOnKeyListener { _, _, event -> offer(event);true } }.asFlow()
fun TextView.textChange() = BroadcastChannel<CharSequence?>(1).apply { doOnTextChanged { text, _, _, _ -> offer(text) } }.asFlow()
fun TextView.beforeTextChange() = BroadcastChannel<CharSequence?>(1).apply { doBeforeTextChanged { text, _, _, _ -> offer(text) } }.asFlow()
fun TextView.afterTextChange() = BroadcastChannel<CharSequence?>(1).apply { doAfterTextChanged { offer(it) } }.asFlow()
fun CompoundButton.checked() = BroadcastChannel<Boolean>(1).apply { setOnCheckedChangeListener { _, isChecked -> offer(isChecked) } }.asFlow()

//------------------------------------------
enum class RecyclerViewScroll { START, END }

fun RecyclerView.scrollReached() = callbackFlow<RecyclerViewScroll> {
    addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                when (val manager = recyclerView.layoutManager) {
                    is LinearLayoutManager -> when (manager.orientation) {
                        LinearLayoutManager.VERTICAL -> when {
                            recyclerView.canScrollVertically(-1) -> RecyclerViewScroll.END
                            recyclerView.canScrollVertically(1) -> RecyclerViewScroll.START
                            else -> null
                        }
                        LinearLayoutManager.HORIZONTAL -> when {
                            recyclerView.canScrollHorizontally(-1) -> RecyclerViewScroll.END
                            recyclerView.canScrollHorizontally(1) -> RecyclerViewScroll.START
                            else -> null
                        }
                        else -> null
                    }
                    else -> null
                }?.let(::offer)
            }
        }
    })
    awaitClose()
}