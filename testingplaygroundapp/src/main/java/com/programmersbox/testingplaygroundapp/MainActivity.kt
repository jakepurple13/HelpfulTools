package com.programmersbox.testingplaygroundapp

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.programmersbox.dragswipe.Direction
import com.programmersbox.dragswipe.DragSwipeAdapter
import com.programmersbox.dragswipe.DragSwipeUtils
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.test_item.view.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        testRV.adapter = CustomAdapter(mutableListOf("Hello", "World"))

        DragSwipeUtils.setDragSwipeUp(testRV.adapter as CustomAdapter, testRV, Direction.UP + Direction.DOWN, Direction.START + Direction.END)
    }

    inner class CustomAdapter(dataList: MutableList<String>) : DragSwipeAdapter<String, ViewHolder>(dataList) {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
            ViewHolder(layoutInflater.inflate(R.layout.test_item, parent, false))

        override fun ViewHolder.onBind(item: String, position: Int) {
            itemView.testText.text = item
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

}
