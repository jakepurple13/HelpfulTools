package com.programmersbox.testingplaygroundapp

import android.content.Context
import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import com.programmersbox.helpfulutils.layoutInflater
import com.programmersbox.helpfulutils.postDelayed
import com.programmersbox.helpfulutils.sizedListOf
import com.programmersbox.loggingutils.Loged
import com.programmersbox.loggingutils.f
import com.programmersbox.testingplaygroundapp.databinding.BindingTestItemBinding
import kotlinx.android.synthetic.main.activity_binding.*

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

    }
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
