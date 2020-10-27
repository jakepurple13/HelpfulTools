package com.programmersbox.funutils.views

import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.programmersbox.dragswipe.DragSwipeAdapter
import com.programmersbox.funutils.databinding.TableAdapterItemBinding
import com.programmersbox.helpfulutils.layoutInflater

sealed class TableModel<T>(val list: List<Pair<CharSequence, T>?>) {
    /**
     * A header model
     */
    class HeaderModel<T>(list: List<Pair<CharSequence, T>?>) : TableModel<T>(list) {
        constructor(vararg items: Pair<CharSequence, T>?) : this(items.toList())
    }

    /**
     * A cell model
     */
    class CellModel<T>(list: List<Pair<CharSequence, T>?>) : TableModel<T>(list) {
        constructor(vararg items: Pair<CharSequence, T>?) : this(items.toList())
    }

    internal var tableAdapterCreator: TableAdapterCreator<T>? = null

    /**
     * Use this if you want to change the layout weight of each of the items
     */
    var weightArray: FloatArray? = null

    internal fun onClick(textView: List<TextView>) {
        tableAdapterCreator?.let { t ->
            val block = when (this) {
                is HeaderModel<*> -> t::headerClick
                is CellModel<*> -> t::cellClick
            }

            textView.forEachIndexed { index, textView -> list[index]?.second?.let { s -> textView.setOnClickListener { block(textView, s) } } }
        }
    }
}

interface TableAdapterCreator<T> {
    /**
     * set header theming/styling/anything else
     */
    fun setHeader(textView: TextView, columnPosition: Int) {}

    /**
     * set cell theming/styling/anything else
     */
    fun setCell(textView: TextView, rowPosition: Int) {}

    /**
     * set header click
     */
    fun headerClick(textView: TextView, item: T) {}

    /**
     * set cell click
     */
    fun cellClick(textView: TextView, item: T) {}
}

class TableAdapter<T>(
    private val tableAdapterCreator: TableAdapterCreator<T> = object : TableAdapterCreator<T> {}
) : DragSwipeAdapter<TableModel<T>, CustomTableHolder<T>>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomTableHolder<T> =
        CustomTableHolder(TableAdapterItemBinding.inflate(parent.context.layoutInflater, parent, false))

    override fun CustomTableHolder<T>.onBind(item: TableModel<T>, position: Int) = setBinding(item, tableAdapterCreator)

    /**
     * Add cells
     */
    fun addCells(vararg items: Pair<CharSequence, T>?) = addItem(TableModel.CellModel(*items))

    /**
     * Add headers
     */
    fun addHeader(vararg items: Pair<CharSequence, T>?) = addItem(TableModel.HeaderModel(*items))
}

class CustomTableHolder<T>(private val binding: TableAdapterItemBinding) : RecyclerView.ViewHolder(binding.root) {
    fun setBinding(item: TableModel<T>, creator: TableAdapterCreator<T>) {
        item.tableAdapterCreator = creator
        binding.model = item
        binding.creator = creator
        binding.position = adapterPosition
        binding.executePendingBindings()
    }
}

@BindingAdapter("createColumns", "creator", "position")
fun columnCreator(layout: LinearLayout, model: TableModel<*>, creator: TableAdapterCreator<*>, position: Int) {
    if (model.weightArray?.size ?: model.list.size != model.list.size) throw IndexOutOfBoundsException("WeightArray must have the same number of arguments as the cell count")
    val block = when (model) {
        is TableModel.HeaderModel<*> -> creator::setHeader
        is TableModel.CellModel<*> -> creator::setCell
    }

    layout.weightSum = model.weightArray?.sum() ?: model.list.size.toFloat()
    model.list
        .map {
            TextView(layout.context).apply {
                text = it?.first
                gravity = Gravity.CENTER
            }.apply { it?.let { block(this, position) } }
        }
        .also(model::onClick)
        .forEachIndexed { index, textView ->
            val w = model.weightArray?.get(index) ?: 1.0f
            layout.addView(textView, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, w))
        }
}