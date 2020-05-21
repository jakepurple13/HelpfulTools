package com.programmersbox.testingplaygroundapp

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
