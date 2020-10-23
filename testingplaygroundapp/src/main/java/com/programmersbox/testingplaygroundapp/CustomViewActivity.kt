package com.programmersbox.testingplaygroundapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.animation.OvershootInterpolator
import android.widget.RelativeLayout
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import com.google.android.material.snackbar.Snackbar
import com.programmersbox.funutils.views.animateTo0
import com.programmersbox.helpfulutils.battery
import com.programmersbox.helpfulutils.batteryIntentFilter
import com.programmersbox.helpfulutils.nextColor
import kotlinx.android.synthetic.main.activity_custom_view.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.random.Random

class CustomViewActivity : AppCompatActivity() {

    private val battery by lazy { battery(false) { loading.progress = it.percent.toInt() } }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_view)

        loading.progressColor = Random.nextColor(255)
        loading.emptyColor = Random.nextColor(255)
        loading.setImageResource(R.drawable.ace1)
        loading.setImageBitmap(getDrawable(R.drawable.ace3)?.toBitmap())
        loading.setImageDrawable(getDrawable(R.drawable.ace2))

        loading.animationDuration(2500L)
        loading.animateInterpolator(OvershootInterpolator())

        startButton.setOnClickListener {
            //loading.animateTo(100, 0)
            GlobalScope.launch {
                var count = 0
                while (count <= 100) {
                    delay(50)
                    runOnUiThread {
                        loading.progress = ++count
                    }
                }
                runOnUiThread {
                    Snackbar.make(loading, "Finished", Snackbar.LENGTH_SHORT).show()
                }
            }
        }

        startButton.setOnLongClickListener {
            loading.animateTo0()
            true
        }

        val batteryConnect = MutableStateFlow(false)

        GlobalScope.launch {
            batteryConnect.collect {
                try {
                    if (it) {
                        registerReceiver(battery, batteryIntentFilter())
                    } else {
                        unregisterReceiver(battery)
                    }
                } catch (e: Exception) {
                    //e.printStackTrace()
                }
            }
        }

        batteryButton.setOnClickListener {
            batteryConnect.value = !batteryConnect.value
            batteryButton.text = "Battery: ${batteryConnect.value}"
        }

        batteryButton.setOnLongClickListener {
            loading.progressColor = Random.nextColor()
            loading.emptyColor = Random.nextColor()
            true
        }

        progressSlider.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                loading.progress = progress
                startButton.text = "Start: $progress"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}

        })

        lineWidthChanger.setListener { fl, _ -> loading.loadingWidth = fl }

        heightSlider.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val params: RelativeLayout.LayoutParams = loading.layoutParams as RelativeLayout.LayoutParams
                params.height = 100 * progress
                loading.layoutParams = params
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}

        })
    }
}
