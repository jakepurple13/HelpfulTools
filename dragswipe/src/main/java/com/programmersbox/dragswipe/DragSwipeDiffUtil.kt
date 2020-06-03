package com.programmersbox.dragswipe

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

open class DragSwipeDiffUtil<T>(private val oldList: List<T>, private val newList: List<T>) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size
    override fun getNewListSize(): Int = newList.size

    open fun areItemsTheSame(oldItem: T, newItem: T): Boolean = oldItem == newItem
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        areItemsTheSame(oldList[oldItemPosition], newList[newItemPosition])

    open fun areContentsTheSame(oldItem: T, newItem: T): Boolean = oldItem === newItem
    override fun areContentsTheSame(oldPosition: Int, newPosition: Int): Boolean = areContentsTheSame(oldList[oldPosition], newList[newPosition])
}

fun <T, R : DragSwipeDiffUtil<T>> DragSwipeAdapter<T, *>.setData(newList: List<T>, diffUtil: (oldList: List<T>, newList: List<T>) -> R) {
    val diffResult = DiffUtil.calculateDiff(diffUtil(dataList, newList))
    dataList.clear()
    dataList.addAll(newList)
    diffResult.dispatchUpdatesTo(this)
}

abstract class DragSwipeDiffUtilAdapter<T, VH : RecyclerView.ViewHolder>(dataList: MutableList<T> = mutableListOf()) :
    DragSwipeAdapter<T, VH>(dataList) {

    protected abstract val dragSwipeDiffUtil: (oldList: List<T>, newList: Collection<T>) -> DragSwipeDiffUtil<T>

    override fun setListNotify(genericList: Collection<T>) {
        val diffResult = DiffUtil.calculateDiff(dragSwipeDiffUtil(dataList, genericList))
        dataList.clear()
        dataList.addAll(genericList)
        diffResult.dispatchUpdatesTo(this)
    }

}
