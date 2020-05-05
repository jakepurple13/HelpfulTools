# [DragSwipe](https://github.com/jakepurple13/HelpfulTools/tree/master/dragswipe/src/main/java/com/programmersbox/dragswipe)

#### EASILY add drag-and-drop and/or swipe to ANY RecyclerView

This library was made so that you can do whatever you want to an adapter while implementing drag-and-drop/swipe capabilities. After seeing all these tutorials that add 20-million things to your code in order to implement drag and drop/swiping, I tried to make this as simple as possible, so that you, the developer, need to call the least number of lines in order to activate drag-and-drop/swipe.

[![](https://jitpack.io/v/jakepurple13/HelpfulTools.svg)](https://jitpack.io/#jakepurple13/HelpfulTools)
```gradle
	implementation 'com.github.jakepurple13.HelpfulTools:dragswipe:{version}'
```

1. First: extend DragSwipeAdapter
	
    ```kotlin
    class TestAdapter(stuff: MutableList<Int>): DragSwipeAdapter<Int, ViewHolder>(stuff)
    ```
2. Call the DragSwipeUtils.setDragSwipe method

	```kotlin
    DragSwipeUtils.setDragSwipeUp(
            adapter,
            recyclerView,
            Direction.UP + Direction.DOWN + Direction.START + Direction.END,
            Direction.START + Direction.END)
    ```
    
    
3. And You're done! Drag-and-Drop/Swipe to your hearts content!

If you want to remove drag/swipe from a recyclerview, I got you covered! ```DragSwipeUtils.setDragSwipe``` returns a DragSwipeHelper, hold that variable and call ```DragSwipeUtils.disableDragSwipe``` while passing in that variable, and no more drag/swipe.

```kotlin
	//this will enable drag/drop/swipe on creation
    val helper = DragSwipeUtils.setDragSwipeUp(
            adapter,
            recyclerView,
            Direction.UP + Direction.DOWN + Direction.START + Direction.END,
            Direction.START + Direction.END)
    //to disable        
    DragSwipeUtils.disableDragSwipe(helper)
    //to enable again
    DragSwipeUtils.enableDragSwipe(helper, recyclerView)
```

# Advanced
If you want, for example, something behind the swipe, extend 
```kotlin
DragSwipeManageAdapter
```
and you can customize even further.

## Custom Drag and Swipe Views
Do you want to choose what view activates dragging and swiping? I've thought of that too!

When you add DragSwipeActions:
```kotlin
override fun isLongPressDragEnabled(): Boolean = false  
  
override fun isItemViewSwipeEnabled(): Boolean {  
    return false  
}
```
Then, pass in the helper into your adapter and then call:
```kotlin
holder.dragImage.setOnTouchListener { _, _ ->  
	helper!!.startDrag(holder)  
    false  
}

holder.title.setOnTouchListener { _, _ ->  
	helper!!.startSwipe(holder)  
    false  
}
```
On the item(s) you want to control the drag or swipe.

## Choose What Rows Have Certain Drags and Swipes
Just override the getMovementFlags method! I even included a makeMovementFlags method to make it even easier!
```kotlin
override fun getMovementFlags(  
    recyclerView: RecyclerView,  
	viewHolder: RecyclerView.ViewHolder  
): Int? {  
    return if (viewHolder.adapterPosition % 10 == 0)  
        makeMovementFlags(swipeDirs = Direction.END + Direction.UP)  
    else  
		super.getMovementFlags(recyclerView, viewHolder)  
}
```

# Extensions!
I have also included some fun extensions!

These add some small fun things.
```kotlin
    val adapter = DragSwipeAdapter<String, ViewHolder>()
    
    //...
    
    adapter.shuffleItems() //to shuffle the adapter with included animation
    adapter.getFirstItem() //gets the first item in the list
    adapter.getMiddleItem() //gets the middle item in the list
    adapter.getLastItem() //gets the last item in the list
    
    adapter[5] = "Hello"
    
    val location = adapter["Hello"]
    
    adapter[5..6] = mutableListOf("Hi", "World")
    
    adapter += "Bye"
    adapter -= "Bye"
    
    adapter += mutableListOf("Hi", "World")
    adapter -= mutableListOf("Hi", "World")
    
    val bool = "Hello" in adapter
    
    for(i in adapter) {
        //...
    }
    
    val recyclerManager = RecyclerViewDragSwipeManager(recyclerView, dragSwipeHelper)
    recyclerManager.dragSwipeEnabled = true
    recyclerManager.dragSwipeEnabled = false
```

# Thank You
Huge thanks to [Paul Burke](https://medium.com/@ipaulpro/drag-and-swipe-with-recyclerview-b9456d2b1aaf) for his wonderful and easy to understand tutorial!
