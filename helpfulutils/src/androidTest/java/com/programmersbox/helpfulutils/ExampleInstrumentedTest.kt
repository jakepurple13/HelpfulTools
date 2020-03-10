package com.programmersbox.helpfulutils

import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.random.Random

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.programmersbox.helpfulutils.test", appContext.packageName)
    }

    @Test
    fun otherUtil() {
        println(Random.nextColor())
        println(DeviceInfo.Info())
        println(DeviceInfo.RuntimeInfo())
        println(0x0000FF.toARGB().toColor())
        println(0x0000FF.toRGB().toColor())
        println(0x0000FF.toCMYK().toColor())
        println(0x0000FF.toARGB().toRGB())
        println(0x0000FF.toRGB().toARGB())
        println(0x0000FF.toCMYK().toRGB())
        println(0x0000FF.toARGB().toCMYK())
        println(0x0000FF.toRGB().toCMYK())
        println(0x0000FF.toCMYK().toARGB())
    }

    @Test
    fun sharedPref() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        appContext.defaultSharedPref.edit().putBoolean("boolean", true).commit()
        appContext.defaultSharedPrefName = "Something Else"
    }

    @Test
    fun viewUtil() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val view = View(appContext)
        view.postDelayed(1000) { }
        val color = appContext.colorFromTheme(R.attr.actionMenuTextColor)
        println(color)
        val linearLayout = LinearLayout(appContext)
        linearLayout.addView(view)
        linearLayout.animateChildren {
            view.visibility = View.GONE
        }
        val recycle = RecyclerView(appContext)
        recycle.quickAdapter<String>()
        recycle.quickAdapter(R.layout.support_simple_spinner_dropdown_item, "Hello", "World") {
            //this is to render the view
            println(it)
        }
        recycle.adapter = QuickAdapter<String>(appContext)
        QuickAdapter<String>(appContext).add(R.layout.support_simple_spinner_dropdown_item, "Hello") {
            println(it)
        }

        val quick = QuickAdapter<String>(appContext)
        quick.add(R.layout.support_simple_spinner_dropdown_item, "Hello", "World") {
            println(it)
        }

        val item = quick[0]
        println(item)
        quick[0] = "Goodbye"
        println(quick[0])
        println(quick.dataList)

        runOnUIThread {
            println("On the ui thread now!")
        }

    }
}