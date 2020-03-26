package com.programmersbox.helpfultools

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.programmersbox.dragswipe.DragSwipeAdapter
import com.programmersbox.flowutils.*
import com.programmersbox.gsonutils.fromJson
import com.programmersbox.gsonutils.getJsonApi
import com.programmersbox.gsonutils.toPrettyJson
import com.programmersbox.helpfulutils.*
import com.programmersbox.loggingutils.*
import com.programmersbox.rxutils.invoke
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_item.view.*
import kotlinx.android.synthetic.main.layout_item_two.view.*
import kotlinx.coroutines.flow.map
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private val compositeDisposable = CompositeDisposable()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //----------------------------------------------
        val flowItem = FlowItem(10)
        flowItem.bindToUI(flowItemValue) { text = "FlowItem Value: $it" }
        flowItemChange.setOnClickListener {
            flowItem(Random.nextInt(1, 100))
            //either one will work
            //flowItem.setValue(Random.nextInt(1, 100))
        }
        //----------------------------------------------
        //usually put these or any other Loged modifiers in the application class
        Loged.FILTER_BY_PACKAGE_NAME = "com.programmersbox.helpfultools"
        Loged.TAG = "HelpfulTools"

        logedInfo.setOnClickListener {
            //These will do normal logs
            Loged.w("Hello World")
            Loged.a("Hello World")
            Loged.i("Hello World")
            Loged.v("Hello World")
            Loged.e("Hello World")
            Loged.d("Hello World")
            Loged.wtf("Hello World")
            Loged.r("Hello World")
            //These will put a box around the log
            Loged.f("Hello World")
            Loged.fw("Hello World")
            Loged.fa("Hello World")
            Loged.fi("Hello World")
            Loged.fv("Hello World")
            Loged.fe("Hello World")
            Loged.fd("Hello World")
        }
        //----------------------------------------------
        gsonInfo.setOnClickListener {
            Log.d("Gson", DeviceInfo.Info().toPrettyJson())
            Log.d("Gson", DeviceInfo.Info().toPrettyJson().fromJson<DeviceInfo.Info>().toString())
        }
        //----------------------------------------------
        val colorPublisher = PublishSubject.create<Int>()
        val colorApiPublishSubject = PublishSubject.create<String>()
        colorApiPublishSubject
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .map {
                try {
                    getJsonApi<ColorApi>("http://thecolorapi.com/id?hex=${it.drop(2)}")
                } catch (e: Exception) {
                    colorApiBlack.copy(hex = Hex(it, it))
                }?.hex?.value
            }
            .subscribe { colorInformation.text = "#$it" }
            .addTo(compositeDisposable)
        colorPublisher
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe {
                colorInfo.setBackgroundColor(it)
                colorApiPublishSubject(Integer.toHexString(it))
            }
            .addTo(compositeDisposable)
        colorInfo
            .clicks() //Flow Binding
            .map { Random.nextColor().also { println(it.toHexString()) } }
            .collectOnUi(colorPublisher::onNext)
        colorInformation
            .longClicks() //Flow Binding
            .map { colorInformation.text }
            .collectOnUi(::println)
        //----------------------------------------------
        var showOrNot = true
        viewInfo.setOnClickListener {
            if (showOrNot) {
                getDrawable(R.drawable.ic_launcher_foreground)
            } else {
                null
            }.let {
                viewValue.startDrawable = it
                viewValue.endDrawable = it
                viewValue.topDrawable = it
                viewValue.bottomDrawable = it
            }
            showOrNot = !showOrNot
        }
        //----------------------------------------------
        recyclerView.quickAdapter(R.layout.layout_item, "Hello", "World") {
            textView.text = it
            setOnClickListener { _ -> println(it) }
        }

        recyclerView.quickAdapter(R.layout.layout_item_two, Names.names.randomRemove(), Names.names.randomRemove()) {
            textView2.text = it
            setOnClickListener { _ -> println(it) }
        }

        @Suppress("UNCHECKED_CAST")
        (recyclerView.adapter as? QuickAdapter<String>)?.add(R.layout.layout_item, Names.names.randomRemove(), Names.names.randomRemove()) {
            textView.text = it
            setOnClickListener { _ ->

                println(it)

                (recyclerView.adapter as QuickAdapter<String>)[0] = try {
                    Names.names.randomRemove()
                } catch (e: IndexOutOfBoundsException) {
                    "Hello"
                }
            }
        }

        requestPermissions(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET
        ) {
            println(it)
        }

        //DragSwipeUtils.setDragSwipeUp(adapter, recyclerView, Direction.UP + Direction.DOWN, Direction.START + Direction.END)

        biometricUse.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                requestPermissions(Manifest.permission.USE_BIOMETRIC) {
                    if (it.isGranted) {
                        val buttonSet = fun(s: String) { biometricUse.text = s }
                        BiometricBuilder.biometricBuilder(this) {
                            authError { _, _ -> buttonSet("Auth Error") }
                            authSuccess { buttonSet("Success") }
                            authFailed { buttonSet("Auth Failed") }
                            error { result -> Toast.makeText(this@MainActivity, result.reason, Toast.LENGTH_LONG).show() }
                            promptInfo {
                                title = "Title"
                                subtitle = "Subtitle"
                                description = "Description"
                                negativeButton = null
                                confirmationRequired = true
                                deviceCredentialAllowed = true
                            }
                        }
                    }
                }
            }
        }

    }

    inner class CustomAdapter(dataList: MutableList<String>) : DragSwipeAdapter<String, ViewHolder>(dataList) {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
            ViewHolder(layoutInflater.inflate(R.layout.layout_item, parent))

        override fun ViewHolder.onBind(item: String, position: Int) {
            itemView.textView.text = item
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}
