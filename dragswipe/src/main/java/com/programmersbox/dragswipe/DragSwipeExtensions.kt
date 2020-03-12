package com.programmersbox.dragswipe

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import java.util.*
import kotlin.random.Random

/**
 * Shuffles items in the adapter and notifies
 */
fun <T, VH : RecyclerView.ViewHolder> DragSwipeAdapter<T, VH>.shuffleItems() {
    for (i in dataList.indices) {
        val num = Random.nextInt(0, dataList.size - 1)
        Collections.swap(dataList, i, num)
        notifyItemMoved(i, num)
        notifyItemChanged(i)
        notifyItemChanged(num)
    }
}

/**
 * gets the first item in [DragSwipeAdapter.dataList]
 */
val <T, VH : RecyclerView.ViewHolder> DragSwipeAdapter<T, VH>.first get(): T? = dataList.firstOrNull()

/**
 * gets the middle item in [DragSwipeAdapter.dataList]
 */
val <T, VH : RecyclerView.ViewHolder> DragSwipeAdapter<T, VH>.middle get(): T? = dataList.getOrNull(itemCount / 2)

/**
 * gets the last item in [DragSwipeAdapter.dataList]
 */
val <T, VH : RecyclerView.ViewHolder> DragSwipeAdapter<T, VH>.last get(): T? = dataList.lastOrNull()

/**
 * @see ItemTouchHelper.Callback.makeFlag
 */
@Suppress("unused")
fun <T, VH : RecyclerView.ViewHolder> DragSwipeActions<T, VH>.makeFlag(
    state: Int,
    direction: Int
): Int = ItemTouchHelper.Callback.makeFlag(state, direction)

/**
 * gets the [num] item from [DragSwipeAdapter.dataList]
 */
operator fun <T, VH : RecyclerView.ViewHolder> DragSwipeAdapter<T, VH>.get(num: Int): T = dataList[num]

/**
 * Gets the location of [element] in [DragSwipeAdapter.dataList]
 */
operator fun <T, VH : RecyclerView.ViewHolder> DragSwipeAdapter<T, VH>.get(element: T): Int = dataList.indexOf(element)

/**
 * sets the [num] location of [DragSwipeAdapter.dataList] to [element]
 */
operator fun <T, VH : RecyclerView.ViewHolder> DragSwipeAdapter<T, VH>.set(num: Int, element: T) {
    dataList[num] = element
    notifyItemChanged(num)
}

/**
 * sets the [num] locations of [DragSwipeAdapter.dataList] to [element]
 */
operator fun <T, VH : RecyclerView.ViewHolder> DragSwipeAdapter<T, VH>.set(num: IntRange, element: List<T>) {
    for ((i, j) in num.withIndex()) {
        dataList[j] = element[i]
    }
    notifyItemRangeChanged(num.first, num.count())
}

/**
 * adds a list of [elements] to [DragSwipeAdapter.dataList]
 */
operator fun <T, VH : RecyclerView.ViewHolder> DragSwipeAdapter<T, VH>.plusAssign(elements: List<T>) = addItems(elements)

/**
 * adds an [element] to [DragSwipeAdapter.dataList]
 */
operator fun <T, VH : RecyclerView.ViewHolder> DragSwipeAdapter<T, VH>.plusAssign(element: T) = addItem(element)

/**
 * removes a list of [element] from [DragSwipeAdapter.dataList]
 */
operator fun <T, VH : RecyclerView.ViewHolder> DragSwipeAdapter<T, VH>.minusAssign(element: List<T>) {
    val intList = arrayListOf<Int>()
    for (i in dataList.withIndex())
        if (i == element)
            intList += i.index
    for (i in intList)
        removeItem(i)
}

/**
 * removes [element] from [DragSwipeAdapter.dataList]
 */
operator fun <T, VH : RecyclerView.ViewHolder> DragSwipeAdapter<T, VH>.minusAssign(element: T) {
    removeItem(dataList.indexOf(element))
}

/**
 * checks if [element] is in [DragSwipeAdapter.dataList]
 */
operator fun <T, VH : RecyclerView.ViewHolder> DragSwipeAdapter<T, VH>.contains(element: T): Boolean = element in dataList

/**
 * allows iteration of [DragSwipeAdapter.dataList]
 */
operator fun <T, VH : RecyclerView.ViewHolder> DragSwipeAdapter<T, VH>.iterator() = dataList.iterator()

/**
 * @see [DragSwipeUtils.enableDragSwipe]
 */
fun RecyclerView.attachDragSwipeHelper(dragSwipeHelper: DragSwipeHelper) = DragSwipeUtils.enableDragSwipe(dragSwipeHelper, this)

/**
 * @see [DragSwipeUtils.disableDragSwipe]
 */
fun RecyclerView.removeDragSwipeHelper(dragSwipeHelper: DragSwipeHelper) = DragSwipeUtils.disableDragSwipe(dragSwipeHelper)

/**
 * A nice little manager that will take care of enabling and disabling dragswipe actions dynamically
 *
 * @param recyclerView the recyclerView you want to be attached to
 * @param dragSwipeHelper the helper you want to use on the [recyclerView]
 */
class RecyclerViewDragSwipeManager(private val recyclerView: RecyclerView, dragSwipeHelper: DragSwipeHelper? = null) {

    /**
     * The helper! Set this in order to use this this class well!
     */
    var dragSwipeHelper: DragSwipeHelper? = dragSwipeHelper
        set(value) {
            if (field != null) {
                disableDragSwipe()
            }
            field = value
            setDragSwipe()
        }

    /**
     * if true, it will enable DragSwipe
     * if false, it will disable DragSwipe
     */
    var dragSwipedEnabled: Boolean = true
        set(value) {
            field = value
            setDragSwipe()
        }

    private fun setDragSwipe() {
        dragSwipeHelper?.let {
            when (dragSwipedEnabled) {
                true -> enableDragSwipe()
                false -> disableDragSwipe()
            }
        }
    }

    private fun disableDragSwipe() = DragSwipeUtils.disableDragSwipe(dragSwipeHelper!!)
    private fun enableDragSwipe() = DragSwipeUtils.enableDragSwipe(dragSwipeHelper!!, recyclerView)
}

/**
 * @see DragSwipeUtils.setDragSwipeUp
 */
fun <T, VH : RecyclerView.ViewHolder> RecyclerView.setDragSwipeUp(
    dragSwipeAdapter: DragSwipeAdapter<T, VH>,
    dragDirs: Int = Direction.NOTHING.value,
    swipeDirs: Int = Direction.NOTHING.value,
    dragSwipeActions: DragSwipeActions<T, VH>? = null
): DragSwipeHelper {
    check(adapter is DragSwipeAdapter<*, *>) { throw IllegalStateException("Adapter is not a DragSwipeAdapter") }
    return DragSwipeUtils.setDragSwipeUp(dragSwipeAdapter, this, dragDirs, swipeDirs, dragSwipeActions)
}

/**
 * @see DragSwipeUtils.setDragSwipeUp
 */
fun <T, VH : RecyclerView.ViewHolder> RecyclerView.setDragSwipeUp(
    dragDirs: Int = Direction.NOTHING.value,
    swipeDirs: Int = Direction.NOTHING.value,
    dragSwipeActions: DragSwipeActions<T, VH>? = null
): DragSwipeHelper {
    check(adapter is DragSwipeAdapter<*, *> && adapter != null) { throw IllegalStateException("Adapter is not a DragSwipeAdapter") }
    return DragSwipeUtils.setDragSwipeUp(adapter as DragSwipeAdapter<T, VH>, this, dragDirs, swipeDirs, dragSwipeActions)
}

/**
 * @see DragSwipeUtils.setDragSwipeUp
 */
fun <T, VH : RecyclerView.ViewHolder> RecyclerView.setDragSwipeUp(
    callback: DragSwipeManageAdapter<T, VH>,
    dragSwipeActions: DragSwipeActions<T, VH>? = null
): DragSwipeHelper {
    check(adapter is DragSwipeAdapter<*, *>) { throw IllegalStateException("Adapter is not a DragSwipeAdapter") }
    return DragSwipeUtils.setDragSwipeUp(this, callback, dragSwipeActions)
}

/**
 * @see DragSwipeUtils.enableDragSwipe
 */
fun RecyclerView.enableDragSwipe(helper: DragSwipeHelper) = DragSwipeUtils.enableDragSwipe(helper, this)

/**
 * @see DragSwipeUtils.disableDragSwipe
 */
fun RecyclerView.disableDragSwipe(helper: DragSwipeHelper) = DragSwipeUtils.disableDragSwipe(helper)
