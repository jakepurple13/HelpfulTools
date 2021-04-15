package com.programmersbox.dragswipe

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView


@DslMarker
annotation class DragSwipeMarker

/**
 * A dsl way to build [DragSwipeActions]
 * @see DragSwipeActions
 */
class DragSwipeActionBuilder<T> {

    private var moved: (RecyclerView, RecyclerView.ViewHolder, RecyclerView.ViewHolder, DragSwipeAdapter<T, *>) -> Unit =
        { _, viewHolder, target, dragSwipeAdapter -> dragSwipeAdapter.swapItems(viewHolder.absoluteAdapterPosition, target.absoluteAdapterPosition) }

    /**
     * @see DragSwipeActions.onMove
     */
    @DragSwipeMarker
    fun onMove(
        block: (
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder,
            dragSwipeAdapter: DragSwipeAdapter<T, *>
        ) -> Unit
    ) = run { moved = block }

    private var swiped: (RecyclerView.ViewHolder, Direction, DragSwipeAdapter<T, *>) -> Unit =
        { viewHolder, _, dragSwipeAdapter -> dragSwipeAdapter.removeItem(viewHolder.absoluteAdapterPosition) }

    /**
     * @see DragSwipeActions.onSwiped
     */
    @DragSwipeMarker
    fun onSwiped(
        block: (
            viewHolder: RecyclerView.ViewHolder,
            direction: Direction,
            dragSwipeAdapter: DragSwipeAdapter<T, *>
        ) -> Unit
    ) = run { swiped = block }

    private var movementFlags: (RecyclerView, RecyclerView.ViewHolder) -> Int? = { _, _ -> null }

    /**
     * use [makeMovementFlags]
     * @see DragSwipeActions.getMovementFlags
     */
    @DragSwipeMarker
    fun getMovementFlags(block: (recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) -> Int?) = run { movementFlags = block }

    /**
     * This will create the movement flags for you, setting up drag and swipe flags
     * @see ItemTouchHelper.Callback.makeMovementFlags
     */
    fun makeMovementFlags(dragDirs: Int = Direction.NOTHING.value, swipeDirs: Int = Direction.NOTHING.value): Int =
        ItemTouchHelper.Callback.makeMovementFlags(dragDirs, swipeDirs)

    /**
     * @see DragSwipeActions.isLongPressDragEnabled
     */
    @DragSwipeMarker
    var isLongPressDragEnabled: Boolean = true

    /**
     * @see DragSwipeActions.isItemViewSwipeEnabled
     */
    @DragSwipeMarker
    var isItemViewSwipeEnabled: Boolean = true

    private fun build() = object : DragSwipeActions<T> {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder,
            dragSwipeAdapter: DragSwipeAdapter<T, *>
        ) = moved(recyclerView, viewHolder, target, dragSwipeAdapter)

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Direction, dragSwipeAdapter: DragSwipeAdapter<T, *>) =
            swiped(viewHolder, direction, dragSwipeAdapter)

        override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int? = movementFlags(recyclerView, viewHolder)
        override fun isLongPressDragEnabled(): Boolean = isLongPressDragEnabled
        override fun isItemViewSwipeEnabled(): Boolean = isItemViewSwipeEnabled
    }

    companion object {
        @DragSwipeMarker
        fun <T> builder(block: DragSwipeActionBuilder<T>.() -> Unit): DragSwipeActions<T> = DragSwipeActionBuilder<T>().apply(block).build()

        @DragSwipeMarker
        operator fun <T> invoke(block: DragSwipeActionBuilder<T>.() -> Unit): DragSwipeActions<T> = builder(block)
    }
}