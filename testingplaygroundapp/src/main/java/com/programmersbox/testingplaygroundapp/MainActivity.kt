package com.programmersbox.testingplaygroundapp

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.graphics.drawable.Icon
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.RemoteInput
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.programmersbox.dragswipe.*
import com.programmersbox.dslannotations.DslClass
import com.programmersbox.dslannotations.DslField
import com.programmersbox.flowutils.*
import com.programmersbox.funutils.views.flash
import com.programmersbox.gsonutils.sharedPrefObjectDelegate
import com.programmersbox.helpfulutils.*
import com.programmersbox.loggingutils.*
import com.programmersbox.testingplaygroundapp.cardgames.blackjack.BlackjackActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.test_item.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.properties.Delegates
import kotlin.random.Random

var Context.key3: Boolean? by sharedPrefDelegate(null)

class MainActivity : AppCompatActivity() {

    private val adapter = CustomAdapter(sizedListOf(50) { getRandomName() })

    private var keys: String? by sharedPrefDelegate()

    private var batteryInformation: Battery? by sharedPrefObjectDelegate()

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        keys = null
        Loged.fd(keys)
        keys = "Hello"
        Loged.fd(keys)
        key3 = null
        Loged.fe(key3)
        key3 = false
        Loged.fe(key3)

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
            .collectOnUi {
                notificationButton.topDrawable?.changeDrawableColor(Random.nextColor())
                requestPermissions(Manifest.permission.RECORD_AUDIO) {
                    if (it.isGranted) speechToText(object : SpeechListener {
                        override fun getResult(text: ArrayList<String>?) = textToSpeech(text?.firstOrNull())
                    })
                }
                adapter.addItem(getRandomName())
            }

        gotoGames
            .clicks()
            .collectOnUi {
                MaterialAlertDialogBuilder(this)
                    .setTitle("Games to Play")
                    .setEnumItems<Games>(Games.values().map(Games::text).toTypedArray()) { item, _ -> startActivity(Intent(this, item.clazz)) }
                    .show()
            }

        createNotificationChannel("id_channel", importance = NotificationChannelImportance.HIGH)
        createNotificationGroup("id_group")

        notificationButton
            .clicks()
            .collectOnUi {
                sendNotification(
                    R.mipmap.ic_launcher,
                    "Title",
                    "Message",
                    40,
                    "id_channel"
                )
                sendNotification(41) {
                    smallIconId = R.mipmap.ic_launcher
                    title = "Hello"
                    message = "World"
                    channelId = "id_channel"
                    groupSummary = true
                }
                sendNotification(42) {
                    smallIconId = R.mipmap.ic_launcher
                    title = "Hello"
                    message = "World"
                    channelId = "id_channel"
                }
                sendNotification(43) {
                    smallIconId = R.mipmap.ic_launcher
                    title = "Hello"
                    message = "World"
                    channelId = "id_channel"
                    pendingIntent(BlackjackActivity::class.java)
                    addAction {
                        actionTitle = "Action!"
                        actionIcon = R.mipmap.ic_launcher
                        pendingActionIntent(ActionService::class.java)
                    }

                    addReplyAction {
                        actionTitle = "Reply!"
                        actionIcon = R.mipmap.ic_launcher
                        resultKey = KEY_TEXT_REPLY
                        label = "Reply here"
                        pendingActionIntent(ReplyService::class.java)
                    }

                    bigTextStyle {
                        bigText = "This is a big text"
                        contentTitle = "Title!"
                        summaryText = "Summary"
                    }
                }
                sendNotification(44) {
                    smallIconId = R.mipmap.ic_launcher
                    title = "Hello"
                    message = "World"
                    channelId = "id_channel"
                    pendingIntent(BlackjackActivity::class.java)
                    addAction {
                        actionTitle = "Action!"
                        actionIcon = R.mipmap.ic_launcher
                        pendingActionIntent(ActionService::class.java)
                    }

                    addBubble {
                        val target = Intent(this@MainActivity, BlackjackActivity::class.java)
                        val bubbleIntent = PendingIntent.getActivity(this@MainActivity, 0, target, 0 /* flags */)
                        bubbleIntent(bubbleIntent)
                        desiredHeight = 600
                        icon = Icon.createWithResource(this@MainActivity, R.mipmap.ic_launcher)
                    }

                    bigTextStyle {
                        bigText = "This is a big text"
                        contentTitle = "Title!"
                        summaryText = "Summary"
                    }
                }
            }

        notificationButton
            .longClicks()
            .collectOnUi {
                sendNotification(
                    R.mipmap.ic_launcher,
                    "Title",
                    "Message",
                    39,
                    "id_channel"
                )

                sendNotification(45) {
                    smallIconId = R.mipmap.ic_launcher
                    title = "Hello"
                    message = "World"
                    channelId = "id_channel"
                    customStyle(DecoratedStyle())
                    remoteViews {
                        landscapeCollapsed(this@MainActivity.packageName, R.layout.collapsed_notification)
                        portraitCollapsed(this@MainActivity.packageName, R.layout.collapsed_notification)
                        landscapeHeadsUp(this@MainActivity.packageName, R.layout.collapsed_notification)
                        portraitHeadsUp(this@MainActivity.packageName, R.layout.collapsed_notification)
                        landscapeExpanded(this@MainActivity.packageName, R.layout.expanded_notification)
                        portraitExpanded(this@MainActivity.packageName, R.layout.expanded_notification)
                    }
                }

                sendNotification(46) {
                    smallIconId = R.mipmap.ic_launcher
                    channelId = "id_channel"
                }
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

        PersonBuilder4.builder {

        }

        NewDsl.buildDsl<Int, String> {

        }

        JavaDslBuilder.javaDslBuild {
            with(it) {
                function { println("Hello") }
                functionOne { println("World") }
                javaName("Java!")
                num(5)
            }
        }

        val flow = FlowItemBuilder.buildFlow<Int> {
            this.item = 5
            collectOnUi { println(it) }
        }
        flow(70)

        battery { println(it) }

        screenOff { context, intent -> println("screen off") }

        screenOn { context, intent -> println("screen on") }

        Loged.f(batteryInfo)

        Loged.fi(batteryInformation)
        batteryInformation = batteryInfo
        Loged.fi(batteryInformation)
    }

    class DecoratedStyle : NotificationStyle() {
        override fun build(): NotificationCompat.Style = NotificationCompat.DecoratedCustomViewStyle()
        override fun buildSdk(): Notification.Style = Notification.DecoratedCustomViewStyle()
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

@DslClass
class PersonBuilder4 {
    @DslField("birthdayParty", comment = "Set what happens on his birthday party")
    var birthday: (Int) -> Int = { it }

    @DslField("setName")
    var name: String? = ""

    @DslField("setDayOfBirth")
    var dayOfBirth: (String?) -> Unit = {}

    var age = 0

    var birthOfDay: String? = ""

    var nextDay: (String?) -> Unit = {}

    private fun build() = Unit//Person(name, age, birthday)

    companion object {
        fun builder(block: PersonBuilder4.() -> Unit) = PersonBuilder4().apply(block).build()
    }

}

@DslMarker
annotation class DslTestMarker

@DslMarker
annotation class DslTest2Marker

@DslClass(dslMarker = DslTestMarker::class)
class NewDsl<T, R> {
    @DslField("itemNumber")
    var numberItem = 4

    @DslField(name = "thingToTest", dslMarker = DslTest2Marker::class, comment = "This is a comment")
    var testThing: () -> Unit = {}
    var runAction: () -> Unit = {}
    var paramOne: (Int, String) -> Unit = { _, _ -> }
    var paramTwo: (Int) -> Unit = {}
    var paramThree: (Int) -> String = { "$it" }
    var paramFour = fun(_: Int) = Unit
    var paramFive = fun(_: T, _: R) = Unit
    var paramSix = fun(_: T) = Unit
    var tItem: T? = null
    var rItem: R? = null
    var checkingItem: (T?) -> Unit = {}
    var itemChecking: (R?) -> T? = { null }

    @DslField(name = "checkItemIntoFlight", dslMarker = DslTest2Marker::class, comment = "This is a comment")
    var checkedItemIn: (R?) -> T? = { null }

    @DslField("setName")
    var name: String? = ""

    private fun build() {
        testThing()
        runAction()
        paramOne(numberItem, paramThree(numberItem))
        paramTwo(numberItem)
        paramFour(numberItem)
        rItem?.let { tItem?.let { it1 -> paramFive(it1, it) } }
        tItem?.let { paramSix(it) }

    }

    companion object {
        fun <T, R> buildDsl(block: NewDsl<T, R>.() -> Unit) = NewDsl<T, R>().apply(block).build()
    }
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

class FlowItemBuilder<T> {

    private var _item: FlowItem<T> by Delegates.notNull()

    var item: T
        get() = _item()
        set(value) {
            _item = FlowItem(value)
        }

    @DslField("collectOnUi")
    var collection: (T) -> Unit = {}
        set(value) = _item.collectOnUI(value).let { Unit }

    private fun build() = _item

    companion object {
        fun <T> buildFlow(block: FlowItemBuilder<T>.() -> Unit) = FlowItemBuilder<T>().apply(block).build()
    }

}

const val KEY_TEXT_REPLY = "key_text_reply"

class ReplyService : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Loged.f("Hello World")
        RemoteInput.getResultsFromIntent(intent)?.getCharSequence(KEY_TEXT_REPLY)?.let { Loged.f("$it is here") }
        context?.notificationManager?.cancelAll()
    }
}

class ActionService : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Loged.f("Hello World")
    }
}