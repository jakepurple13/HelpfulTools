package com.programmersbox.dragswipe

import androidx.recyclerview.widget.DiffUtil

open class DragSwipeDiffUtil<T>(private val oldList: List<T>, private val newList: List<T>) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size
    override fun getNewListSize(): Int = newList.size

    open fun areItemsTheSame(oldItem: T, newItem: T): Boolean = oldItem == newItem
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        areItemsTheSame(oldList[oldItemPosition], newList[newItemPosition])

    open fun areContentsTheSame(oldItem: T, newItem: T): Boolean = oldItem === newItem
    override fun areContentsTheSame(oldPosition: Int, newPosition: Int): Boolean = areContentsTheSame(oldList[oldPosition], newList[newPosition])
}

fun <T, R : DragSwipeDiffUtil<T>> DragSwipeAdapter<T, *>.setData(newList: List<T>, block: (oldList: List<T>, newList: List<T>) -> R) {
    val diffCallback = block(dataList, newList)
    val diffResult = DiffUtil.calculateDiff(diffCallback)
    dataList.clear()
    dataList.addAll(newList)
    diffResult.dispatchUpdatesTo(this)
}