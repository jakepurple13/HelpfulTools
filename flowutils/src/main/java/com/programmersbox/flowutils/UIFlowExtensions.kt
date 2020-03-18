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
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow

fun View.clicks(): Flow<Unit> = BroadcastChannel<Unit>(1).apply { setOnClickListener { offer(Unit) } }.asFlow()
fun View.longClicks(): Flow<Unit> = BroadcastChannel<Unit>(1).apply { setOnLongClickListener { offer(Unit);true } }.asFlow()
fun View.touches(): Flow<MotionEvent> = BroadcastChannel<MotionEvent>(1).apply { setOnTouchListener { _, event -> offer(event);true } }.asFlow()
fun View.drags(): Flow<DragEvent> = BroadcastChannel<DragEvent>(1).apply { setOnDragListener { _, event -> offer(event) } }.asFlow()
fun View.keyPress(): Flow<KeyEvent> = BroadcastChannel<KeyEvent>(1).apply { setOnKeyListener { _, _, event -> offer(event);true } }.asFlow()
fun TextView.textChange() = BroadcastChannel<CharSequence?>(1).apply { doOnTextChanged { text, _, _, _ -> offer(text) } }.asFlow()
fun TextView.beforeTextChange() = BroadcastChannel<CharSequence?>(1).apply { doBeforeTextChanged { text, _, _, _ -> offer(text) } }.asFlow()
fun TextView.afterTextChange() = BroadcastChannel<CharSequence?>(1).apply { doAfterTextChanged(this::offer) }.asFlow()
fun CompoundButton.checked() = BroadcastChannel<Boolean>(1).apply { setOnCheckedChangeListener { _, isChecked -> offer(isChecked) } }.asFlow()
