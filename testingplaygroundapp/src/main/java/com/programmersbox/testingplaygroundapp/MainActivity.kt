package com.programmersbox.testingplaygroundapp

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.programmersbox.dragswipe.*
import com.programmersbox.flowutils.RecyclerViewScroll
import com.programmersbox.flowutils.clicks
import com.programmersbox.flowutils.scrollReached
import com.programmersbox.funutils.views.flash
import com.programmersbox.helpfulutils.setEnumItems
import com.programmersbox.helpfulutils.sizedListOf
import com.programmersbox.loggingutils.Loged
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.test_item.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.properties.Delegates
import kotlin.random.Random

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
                MaterialAlertDialogBuilder(this)
                    .setTitle("Games to Play")
                    .setEnumItems<Games>(Games.values().map(Games::text).toTypedArray()) { item, _ -> startActivity(Intent(this, item.clazz)) }
                    .show()
            }

        val person = PersonBuilder.builder {
            personName(getRandomName())
            personAge(Random.nextInt(1, 50))
            birthdayParty { it + 1 }
        }

        val newAge: (Unit) -> Unit = {
            println(person)
            person.birthday()
            println(person)
        }

        val person2 = PersonBuilder2.builder {
            name(getRandomName())
            age(Random.nextInt(1, 50))
            birthdayParty { it + 1 }
        }

        val newAge2: (Unit) -> Unit = {
            println(person2)
            person2.birthday()
            println(person2)
        }

        val person3 = PersonBuilder3.builder {
            name(getRandomName())
            age(Random.nextInt(1, 50))
            birthdayParty { it + 1 }
        }

        val newAge3: (Unit) -> Unit = {
            println(person3)
            person3.birthday()
            println(person3)
        }

        testRV
            .scrollReached()
            .collectOnUi {
                when (it) {
                    RecyclerViewScroll.START -> Loged.r("Start").also(newAge).also(newAge2).also(newAge3)
                    RecyclerViewScroll.END -> adapter.addItem(getRandomName())
                }
            }

        Log.v("Hello", "World")
        Log.i("Hello", "World")
        Log.d("Hello", "World")
        Log.wtf("Hello", "World")
        Log.w("Hello", "World")
        Log.e("Hello", "World")
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
                .collectOnUi {
                    itemView.flash { i, view -> view.setBackgroundColor(i) }
                    this@CustomAdapter[position] = getRandomName()
                }

            Glide.with(itemView)
                .load(R.mipmap.ic_launcher)
                .into<Drawable> {
                    loadCleared { }
                    resourceReady { image, _ -> itemView.testImage.setImageDrawable(image) }
                }
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}

@DslMarker
annotation class GlideMarker

fun <T> RequestBuilder<T>.into(target: CustomTargetBuilder<T>.() -> Unit) = into(CustomTargetBuilder<T>().apply(target).build())

class CustomTargetBuilder<T> {

    private var resourceReady: (T, Transition<in T>?) -> Unit by Delegates.notNull()

    @GlideMarker
    fun resourceReady(block: (image: T, transition: Transition<in T>?) -> Unit) = run { resourceReady = block }

    private var loadCleared: (Drawable?) -> Unit = {}

    @GlideMarker
    fun loadCleared(block: (placeHolder: Drawable?) -> Unit) = run { loadCleared = block }

    fun build() = object : CustomTarget<T>() {
        override fun onLoadCleared(placeholder: Drawable?) = loadCleared(placeholder)
        override fun onResourceReady(resource: T, transition: Transition<in T>?) = resourceReady(resource, transition)
    }

}