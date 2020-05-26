package com.programmersbox.testingplaygroundapp

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import com.programmersbox.funutils.views.SlideValuePicker
import com.programmersbox.helpfulutils.layoutInflater
import com.programmersbox.helpfulutils.nextColor
import com.programmersbox.helpfulutils.postDelayed
import com.programmersbox.helpfulutils.sizedListOf
import com.programmersbox.loggingutils.Loged
import com.programmersbox.loggingutils.f
import com.programmersbox.testingplaygroundapp.databinding.BindingTestItemBinding
import kotlinx.android.synthetic.main.activity_binding.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlin.random.Random

class BindingActivity : AppCompatActivity() {

    private val adapter = BindAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_binding)

        bindRV.adapter = adapter

        bindRV.postDelayed(1000) {
            adapter.addItems(sizedListOf(10) { BindingTest(getRandomName(), it + 10) })
            Loged.f(adapter.dataList)
        }

        if (Random.nextBoolean()) {
            slidePicker.setOnProgressChangeListener(object : SlideValuePicker.Listener {
                override fun onProgressChanged(progress: Float) {
                    Loged.f(progress)
                }
            })
        } else {
            slidePicker.setOnProgressChangeListener { Loged.f(it) }
        }

        changeColor.setOnClickListener {
            println(slidePicker.progress)
            slidePicker2.setEndColor(Random.nextColor())
            slidePicker2.setStartColor(Random.nextColor())
            slidePicker2.setCircleColor(Random.nextColor())
        }

        slidePicker3.toFlow().collectOnUi { Loged.f(it) }

        slidePicker4.setCheckedListener { println(it) }

    }

    private fun <T> Flow<T>.collectOnUi(action: (T) -> Unit) = GlobalScope.launch { collect { GlobalScope.launch(Dispatchers.Main) { action(it) } } }

}

data class BindingTest(val name: String, val age: Int) : ViewModel()

class BindAdapter(private val context: Context) : BindingDragSwipe<BindingTest, BindHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindHolder =
        BindHolder(BindingTestItemBinding.inflate(context.layoutInflater, parent, false))
}

class BindHolder(binding: BindingTestItemBinding) : BindingViewHolder<BindingTest, BindingTestItemBinding>(binding) {
    override fun setModel(item: BindingTest) {
        binding.model = item
    }
}

class FlowSlidePicker @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    SlideValuePicker(context, attrs, defStyleAttr) {

    private val prog = MutableStateFlow(0.5f)

    fun toFlow() = prog.distinctUntilChanged { old, new -> old == new }

    private fun <T> Flow<T>.collectOnUi(action: (T) -> Unit) = GlobalScope.launch { collect { GlobalScope.launch(Dispatchers.Main) { action(it) } } }

    override fun progressChanged(progress: Float) {
        super.progressChanged(progress)
        prog.value = progress
    }

}

open class SwitchSlidePicker @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    SlideValuePicker(context, attrs, defStyleAttr) {

    var checked = false

    private var listener: Listener? = null

    override fun progressChanged(progress: Float) {
        super.progressChanged(progress)
        checked = when (progress) {
            1.0f -> true
            0.0f -> false
            else -> checked
        }
        listener?.onChecked(checked)
    }

    interface Listener {
        fun onChecked(checked: Boolean)
    }

    fun setCheckedListener(checked: (Boolean) -> Unit) {
        listener = object : Listener {
            override fun onChecked(checked: Boolean) = checked(checked)
        }
    }

}