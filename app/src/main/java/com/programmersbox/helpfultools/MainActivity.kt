package com.programmersbox.helpfultools

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.programmersbox.flowutils.FlowItem
import com.programmersbox.flowutils.bindToUI
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
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private val colorPublisher = PublishSubject.create<Int>()
    private val colorApiPublishSubject = PublishSubject.create<ColorApi>()
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
        colorApiPublishSubject
            .subscribeOn(AndroidSchedulers.mainThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { colorInformation.text = it.toString() }
            .addTo(compositeDisposable)
        colorPublisher
            .subscribeOn(AndroidSchedulers.mainThread())
            .observeOn(Schedulers.io())
            .subscribe {
                runOnUIThread { colorInfo.setBackgroundColor(it) }
                colorApiPublishSubject(getJsonApi<ColorApi>("http://thecolorapi.com/id?hex=${Integer.toHexString(it).drop(2)}"))
            }
            .addTo(compositeDisposable)
        colorInfo.setOnClickListener { colorPublisher.onNext(Random.nextColor().also { println(it.toHexString()) }) }
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
    }
}