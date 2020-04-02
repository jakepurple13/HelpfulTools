package com.programmersbox.testingplaygroundapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.programmersbox.dragswipe.*
import com.programmersbox.flowutils.RecyclerViewScroll
import com.programmersbox.flowutils.clicks
import com.programmersbox.flowutils.scrollReached
import com.programmersbox.helpfulutils.sizedListOf
import com.programmersbox.loggingutils.Loged
import com.programmersbox.testingplaygroundapp.cardgames.blackjack.BlackjackActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.test_item.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val adapter = CustomAdapter(sizedListOf(50) { getRandomName() })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        testRV.adapter = adapter

        DragSwipeUtils.setDragSwipeUp(
            adapter,
            testRV,
            listOf(Direction.UP, Direction.DOWN, Direction.START, Direction.END),
            listOf(Direction.START, Direction.END)
        )

        expandingSlider.setListener { fl, _ -> (testRV.layoutManager as? GridLayoutManager)?.spanCount = fl.toInt() }

        addButton
            .clicks()
            .collectOnUi { adapter.addItem(getRandomName()) }

        gotoGames
            .clicks()
            .collectOnUi {
                AlertDialog.Builder(this)
                    .setItems(Games.values().map(Games::text).toTypedArray()) { _, index ->
                        val toActivity = when (Games.values()[index]) {
                            Games.BLACKJACK -> BlackjackActivity::class.java
                        }
                        startActivity(Intent(this, toActivity))
                    }
                    .setTitle("Games to Play")
                    .show()
            }

        testRV
            .scrollReached()
            .collectOnUi {
                when (it) {
                    RecyclerViewScroll.START -> Loged.r("Start")
                    RecyclerViewScroll.END -> adapter.addItem(getRandomName())
                }
            }
    }

    /**
     * This is hear because we can't find the class for some reason
     */
    private fun <T> Flow<T>.collectOnUi(action: (T) -> Unit) = GlobalScope.launch { collect { GlobalScope.launch(Dispatchers.Main) { action(it) } } }

    inner class CustomAdapter(dataList: MutableList<String>) : DragSwipeAdapter<String, ViewHolder>(dataList) {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
            ViewHolder(layoutInflater.inflate(R.layout.test_item, parent, false))

        override fun ViewHolder.onBind(item: String, position: Int) {
            itemView.testText.text = item
            itemView
                .clicks()
                .collectOnUi { this@CustomAdapter[position] = getRandomName() }
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}
