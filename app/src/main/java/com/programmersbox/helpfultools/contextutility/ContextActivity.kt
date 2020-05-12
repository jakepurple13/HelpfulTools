package com.programmersbox.helpfultools.contextutility

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.RemoteInput
import com.programmersbox.gsonutils.sharedPrefObjectDelegate
import com.programmersbox.helpfultools.R
import com.programmersbox.helpfultools.randomNumber
import com.programmersbox.helpfulutils.Battery
import com.programmersbox.helpfulutils.NotificationDslBuilder
import com.programmersbox.helpfulutils.batteryInfo
import com.programmersbox.helpfulutils.notificationManager
import com.programmersbox.loggingutils.Loged
import com.programmersbox.loggingutils.f
import kotlinx.android.synthetic.main.activity_context.*

class ContextActivity : AppCompatActivity() {

    private var batteryInformation: Battery? by sharedPrefObjectDelegate()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_context)

        println("Random Number is: $randomNumber")

        println(batteryInformation)
        batteryInformation = batteryInfo
        println(batteryInformation)

        notificationSetup()

    }

    private fun notificationSetup() {
        notificationSend.setOnClickListener {
            notificationManager.notify(34, NotificationDslBuilder.builder(this, channelId = "testChannel", smallIconId = R.mipmap.ic_launcher) {
                title = "Title"
                message = "Message"
                autoCancel = true
                addReplyAction {
                    resultKey = "result"
                    label = "label"
                    actionTitle = "Action Title"
                    actionIcon = R.mipmap.ic_launcher
                    pendingActionIntent(ReplyService::class.java)
                }
                bigTextStyle {
                    summaryText = "Summary"
                    contentTitle = "Content Title"
                    bigText = "Big Text"
                }
            })
        }
    }

}

class ReplyService : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        RemoteInput.getResultsFromIntent(intent)?.getCharSequence("result")?.let { Loged.f(it) }
        context?.notificationManager?.cancel(34)
    }
}