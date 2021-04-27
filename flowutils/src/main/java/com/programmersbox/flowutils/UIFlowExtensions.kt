@file:Suppress("EXPERIMENTAL_API_USAGE")

package com.programmersbox.flowutils

import android.annotation.SuppressLint
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
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

fun View.clicks(): Flow<Unit> = callbackFlow { setOnClickListener { trySend(Unit).isSuccess };awaitClose() }
fun View.longClicks(): Flow<Unit> = callbackFlow { setOnLongClickListener { trySend(Unit).isSuccess;true };awaitClose() }

@SuppressLint("ClickableViewAccessibility")
fun View.touches(): Flow<MotionEvent> = callbackFlow<MotionEvent> { setOnTouchListener { _, event -> trySend(event).isSuccess;true };awaitClose() }
fun View.drags(): Flow<DragEvent> = callbackFlow<DragEvent> { setOnDragListener { _, event -> trySend(event).isSuccess };awaitClose() }
fun View.keyPress(): Flow<KeyEvent> = callbackFlow<KeyEvent> { setOnKeyListener { _, _, event -> trySend(event).isSuccess;true };awaitClose() }
fun TextView.textChange(): Flow<CharSequence?> = callbackFlow { doOnTextChanged { text, _, _, _ -> trySend(text).isSuccess };awaitClose() }
fun TextView.beforeTextChange(): Flow<CharSequence?> = callbackFlow { doBeforeTextChanged { text, _, _, _ -> trySend(text).isSuccess };awaitClose() }
fun TextView.afterTextChange(): Flow<CharSequence?> = callbackFlow<CharSequence?> { doAfterTextChanged { trySend(it).isSuccess };awaitClose() }
fun CompoundButton.checked(): Flow<Boolean> =
    callbackFlow { setOnCheckedChangeListener { _, isChecked -> trySend(isChecked).isSuccess };awaitClose() }

//------------------------------------------
enum class RecyclerViewScroll { START, END }

fun RecyclerView.scrollReached() = callbackFlow<RecyclerViewScroll> {
    addOnScrollListener(object : RecyclerView.OnScrollListener() {
        @SuppressLint("SwitchIntDef")
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
                }?.let { trySend(it).isSuccess }
            }
        }
    })
    awaitClose()
}