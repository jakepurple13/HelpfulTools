package com.programmersbox.testingplaygroundapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_view)

        loading.setAnimationListener { println("Finished") }

        val battery = battery {
            loading.progress = it.percent.toInt()
        }

        loading.progressColor = Random.nextColor()
        loading.emptyColor = Random.nextColor()
        loading.setImageResource(R.drawable.ace1)
        loading.setImageDrawable(getDrawable(R.drawable.ace2))

        startButton.setOnClickListener {
            GlobalScope.launch {
                var count = 0
                while (count <= 100) {
                    delay(50)
                    runOnUiThread { loading.progress = ++count }
                }
                runOnUiThread {
                    Snackbar.make(loading, "Finished", Snackbar.LENGTH_SHORT).show()
                }
            }
        }

        val batteryConnect = MutableStateFlow(false)

        GlobalScope.launch {
            batteryConnect.collect {
                if (it) {
                    registerReceiver(battery, batteryIntentFilter())
                } else {
                    unregisterReceiver(battery)
                }
            }
        }

        batteryButton.setOnClickListener {
            batteryConnect.value = !batteryConnect.value
        }
    }
}
