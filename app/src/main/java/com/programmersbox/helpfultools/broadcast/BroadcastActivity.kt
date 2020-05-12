package com.programmersbox.helpfultools.broadcast

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.programmersbox.flowutils.FlowItem
import com.programmersbox.flowutils.collectOnUi
import com.programmersbox.flowutils.plusAssign
import com.programmersbox.helpfultools.R
import com.programmersbox.helpfulutils.*
import kotlinx.android.synthetic.main.activity_broadcast.*
import kotlinx.coroutines.flow.combine
import java.text.SimpleDateFormat
import java.util.*

class BroadcastActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_broadcast)
        setTimeTick()
        setBatteryReceiver()
        setScreenReceiver()
    }

    private fun setTimeTick() {
        var timeTimer: BroadcastReceiver? = null
        timeReceiver.setOnClickListener {
            timeTimer = timeTick { _, _ ->
                runOnUiThread { timeTickText.text = SimpleDateFormat("h:mm a", Locale.getDefault()).format(System.currentTimeMillis()) }
            }
        }

        untimeReceiver.setOnClickListener { timeTimer?.let(this::unregisterReceiver) }
    }

    private fun setBatteryReceiver() {
        var battery: BroadcastReceiver? = null
        batteryReceiver.setOnClickListener { battery = battery { runOnUiThread { batteryText.text = it.toString() } } }
        unbatteryReceiver.setOnClickListener { battery?.let(this::unregisterReceiver) }
    }

    @SuppressLint("SetTextI18n")
    private fun setScreenReceiver() {
        var screenState: BroadcastReceiver? = null
        var screenOn: BroadcastReceiver? = null
        var screenOff: BroadcastReceiver? = null

        val screenOnFlow = FlowItem(0)
        val screenOffFlow = FlowItem(0)

        combine(screenOnFlow.flow, screenOffFlow.flow) { on, off -> on to off }
            .collectOnUi { screenText.text = "Screen On: ${it.first} | Screen Off: ${it.second}" }

        screenReceiver.setOnClickListener {
            screenOn = screenOn { _, _ -> screenOnFlow += 1 }
            screenOff = screenOff { _, _ -> screenOffFlow += 1 }
            screenState = screenState {
                when (it) {
                    ScreenState.ON -> screenOnFlow += 1
                    ScreenState.OFF -> screenOffFlow += 1
                    ScreenState.UNKNOWN -> Unit
                }
            }
        }
        unscreenReceiver.setOnClickListener {
            screenOn?.let(this::unregisterReceiver)
            screenOff?.let(this::unregisterReceiver)
            screenState?.let(this::unregisterReceiver)
        }
    }

}