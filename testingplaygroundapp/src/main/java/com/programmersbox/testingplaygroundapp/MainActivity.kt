package com.programmersbox.testingplaygroundapp

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.programmersbox.dragswipe.Direction
import com.programmersbox.dragswipe.DragSwipeAdapter
import com.programmersbox.dragswipe.DragSwipeUtils
import com.programmersbox.flowutils.RecyclerViewScroll
import com.programmersbox.flowutils.scrollReached
import com.programmersbox.helpfulutils.nextString
import com.programmersbox.loggingutils.Loged
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.test_item.view.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: CustomAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Loged.FILTER_BY_PACKAGE_NAME = "programmersbox"

        val list = mutableListOf<String>().apply { repeat(20) { this += Random.nextString(5) } }

        adapter = CustomAdapter(list)
        testRV.adapter = adapter

        DragSwipeUtils.setDragSwipeUp(adapter, testRV, Direction.UP + Direction.DOWN, Direction.START + Direction.END)

        GlobalScope.launch {
            testRV
                .scrollReached()
                .collect {
                    runOnUiThread {
                        when (it) {
                            RecyclerViewScroll.START -> Loged.r("Start")
                            RecyclerViewScroll.END -> adapter.addItem(Random.nextString(5))
                        }
                    }
                }
        }
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
