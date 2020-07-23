package com.programmersbox.testingplaygroundapp

import android.content.Context
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.programmersbox.dragswipe.DragSwipeAdapter

abstract class BindingViewHolder<T, B : ViewDataBinding>(protected val binding: B) : RecyclerView.ViewHolder(binding.root) {

    /**
     * Call `binding.(model name) = item` here
     * along with any other variable setting you want to do
     */
    abstract fun setModel(item: T)

    fun bind(item: T) {
        setModel(item)
        binding.executePendingBindings()
        render(item)
    }

    open fun render(item: T) = Unit
}

abstract class BindingDragSwipe<T, B : BindingViewHolder<T, *>>(dataList: MutableList<T> = mutableListOf()) : DragSwipeAdapter<T, B>(dataList) {
    override fun B.onBind(item: T, position: Int) = bind(item)
}

class TimeCounter(context: Context, val max: Int, val resetAfter: Long) {
    private var id = 0//counter

    private val prefs by lazy { context.getSharedPreferences("timeCounter$id", Context.MODE_PRIVATE) }

    var count: Int
        get() = prefs.getInt("timeCounterCount", 0)
        private set(value) = if (value <= max) prefs.edit().putInt("timeCounterCount", value).apply() else Unit

    var lastTimeSaved: Long
        get() = prefs.getLong("timeCounterLastTimeSaved", System.currentTimeMillis())
        private set(value) = prefs.edit().putLong("timeCounterLastTimeSaved", value).apply()

    init {
        checkReset()
        counter++
    }

    fun checkReset() {
        if (lastTimeSaved + resetAfter < System.currentTimeMillis()) count = 0
    }

    fun plusOne() {
        count += 1
        if (count == 1)
            lastTimeSaved = System.currentTimeMillis()
    }

    fun belowMax() = count < max
    fun reachedMax() = count >= max

    override fun toString(): String = "TimeCounter(max=$max, resetAfter=$resetAfter, count=$count, lastTimeSaved=$lastTimeSaved)"

    companion object {
        private var counter: Int = 0
    }

}
