package com.programmersbox.helpfulutils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView

/**
 * A quick way to add items to the recyclerview.
 * If the current adapter is not a [QuickAdapter], it will overwrite it with a new [QuickAdapter]
 */
@Suppress("UNCHECKED_CAST")
fun <T> RecyclerView.quickAdapter(@LayoutRes layout: Int? = null, vararg item: T, setup: (View.(T) -> Unit)? = null) {
    adapter = (adapter as? QuickAdapter<T>) ?: QuickAdapter<T>(context)
    if (layout != null && setup != null && item.isNotEmpty()) (adapter as QuickAdapter<T>).add(layout, *item, setup = setup)
}

/**
 * A quick way to make an adapter and add data of a type to it
 * This is a super basic adapter.
 */
class QuickAdapter<T>(private val context: Context) : RecyclerView.Adapter<QuickAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    private class QuickAdapterItem<T>(@LayoutRes val layout: Int, val item: T, val setup: View.(T) -> Unit) {
        fun renderItem(view: View) = view.setup(item)
    }

    private val data = mutableListOf<QuickAdapterItem<T>>()
    val dataList get() = data.map(QuickAdapterItem<T>::item)

    fun add(@LayoutRes layout: Int, vararg item: T, setup: View.(T) -> Unit) = data.addAll(item.map { QuickAdapterItem(layout, it, setup) })
        .also { notifyDataSetChanged() }

    fun remove(index: Int = data.size - 1) = data.removeAt(index).also { notifyItemRemoved(index) }.item
    operator fun contains(item: T) = data.any { it.item == item }
    operator fun get(index: Int) = data[index].item
    operator fun set(index: Int, item: T) {
        data[index] = data[index].let { QuickAdapterItem(it.layout, item, it.setup) }
        notifyItemChanged(index)
    }

    override fun getItemViewType(position: Int): Int = data[position].layout
    override fun getItemCount(): Int = data.size
    override fun onBindViewHolder(holder: ViewHolder, position: Int) = data[position].renderItem(holder.itemView)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(LayoutInflater.from(context).inflate(viewType, parent, false))
}