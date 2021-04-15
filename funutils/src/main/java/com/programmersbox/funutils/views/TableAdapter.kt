package com.programmersbox.funutils.views

import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.CallSuper
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

    internal fun onClick(textView: List<TextView>, position: Int) {
        tableAdapterCreator?.let { t ->
            val block = when (this) {
                is HeaderModel<*> -> t::headerClick
                is CellModel<*> -> t::cellClick
            }

            textView.forEachIndexed { index, textView ->
                list[index]?.second?.let { s -> textView.setOnClickListener { block(textView, s, position, index) } }
            }
        }
    }

    override fun toString(): String = "TableModel.${this::class.java.simpleName}(list=$list)"
}

@DslMarker
annotation class TableAdapterMarker

interface TableAdapterCreator<T> {
    /**
     * set header theming/styling/anything else
     */
    fun setHeader(textView: TextView, rowPosition: Int, columnPosition: Int) {}

    /**
     * set cell theming/styling/anything else
     */
    fun setCell(textView: TextView, rowPosition: Int, columnPosition: Int) {}

    /**
     * set header click
     */
    fun headerClick(textView: TextView, item: T, rowPosition: Int, columnPosition: Int) {}

    /**
     * set cell click
     */
    fun cellClick(textView: TextView, item: T, rowPosition: Int, columnPosition: Int) {}

    class Builder<T> {

        private var headerFormat: (TextView, Int, Int) -> Unit = { _, _, _ -> }
        private var cellFormat: (TextView, Int, Int) -> Unit = { _, _, _ -> }
        private var headerOnClick: (TextView, T, Int, Int) -> Unit = { _, _, _, _ -> }
        private var cellOnClick: (TextView, T, Int, Int) -> Unit = { _, _, _, _ -> }

        /**
         * Format the header
         */
        @TableAdapterMarker
        fun header(format: (tv: TextView, row: Int, column: Int) -> Unit) = apply { headerFormat = format }

        /**
         * Format the cell
         */
        @TableAdapterMarker
        fun cell(format: (tv: TextView, row: Int, column: Int) -> Unit) = apply { cellFormat = format }

        /**
         * Header onClick
         */
        @TableAdapterMarker
        fun headerOnClick(click: (tv: TextView, item: T, row: Int, column: Int) -> Unit) = apply { headerOnClick = click }

        /**
         * Cell onClick
         */
        @TableAdapterMarker
        fun cellOnClick(click: (tv: TextView, item: T, row: Int, column: Int) -> Unit) = apply { cellOnClick = click }

        fun build() = object : TableAdapterCreator<T> {
            override fun setHeader(textView: TextView, rowPosition: Int, columnPosition: Int) = headerFormat(textView, rowPosition, columnPosition)
            override fun headerClick(textView: TextView, item: T, rowPosition: Int, columnPosition: Int) =
                headerOnClick(textView, item, rowPosition, columnPosition)

            override fun setCell(textView: TextView, rowPosition: Int, columnPosition: Int) = cellFormat(textView, rowPosition, columnPosition)
            override fun cellClick(textView: TextView, item: T, rowPosition: Int, columnPosition: Int) =
                cellOnClick(textView, item, rowPosition, columnPosition)
        }

        companion object {
            operator fun <T> invoke(block: Builder<T>.() -> Unit) = Builder<T>().apply(block).build()
        }

    }
}

open class TableAdapter<T>(private val tableAdapterCreator: TableAdapterCreator<T>) : DragSwipeAdapter<TableModel<T>, CustomTableHolder<T>>() {
    constructor(creator: TableAdapterCreator.Builder<T>.() -> Unit = {}) : this(TableAdapterCreator.Builder(creator))

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomTableHolder<T> =
        CustomTableHolder(TableAdapterItemBinding.inflate(parent.context.layoutInflater, parent, false))

    @CallSuper
    override fun CustomTableHolder<T>.onBind(item: TableModel<T>, position: Int) = setBinding(item, tableAdapterCreator)

    /**
     * Add cells
     */
    fun addCells(vararg items: Pair<CharSequence, T>?) = addItem(TableModel.CellModel(*items))

    /**
     * Add headers
     */
    fun addHeader(vararg items: Pair<CharSequence, T>?) = addItem(TableModel.HeaderModel(*items))

    /**
     * Add cell
     */
    fun addCell(item: TableModel.CellModel<T>) = addItem(item)

    /**
     * Add header
     */
    fun addHeader(item: TableModel.HeaderModel<T>) = addItem(item)

    /**
     * get all the items in a specific column
     *
     * # Be Aware
     * if nothing is in a cell, it will return null
     */
    fun getColumn(columnPosition: Int) = dataList.map { it.list.getOrNull(columnPosition)?.second }

    operator fun get(index: Int) = dataList[index].list.map { it?.second }
}

class CustomTableHolder<T>(private val binding: TableAdapterItemBinding) : RecyclerView.ViewHolder(binding.root) {
    fun setBinding(item: TableModel<T>, creator: TableAdapterCreator<T>) {
        item.tableAdapterCreator = creator
        binding.model = item
        binding.creator = creator
        binding.position = absoluteAdapterPosition
        binding.executePendingBindings()
    }
}

@BindingAdapter("createColumns", "creator", "position")
fun columnCreator(layout: LinearLayout, model: TableModel<*>, creator: TableAdapterCreator<*>, position: Int) {
    if (model.weightArray?.size ?: model.list.size != model.list.size) {
        throw IndexOutOfBoundsException("WeightArray must have the same number of arguments as the cell count")
    }
    val block = when (model) {
        is TableModel.HeaderModel<*> -> creator::setHeader
        is TableModel.CellModel<*> -> creator::setCell
    }

    layout.weightSum = model.weightArray?.sum() ?: model.list.size.toFloat()
    model.list
        .mapIndexed { index, pair ->
            TextView(layout.context).apply {
                text = pair?.first
                gravity = Gravity.CENTER
            }.apply { pair?.let { block(this, position, index) } }
        }
        .also { model.onClick(it, position) }
        .forEachIndexed { index, textView ->
            val w = model.weightArray?.get(index) ?: 1.0f
            layout.addView(textView, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, w))
        }
}