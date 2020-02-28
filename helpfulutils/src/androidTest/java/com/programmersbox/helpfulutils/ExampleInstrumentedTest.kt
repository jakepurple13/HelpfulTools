package com.programmersbox.helpfulutils

import android.view.View
import android.widget.LinearLayout
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
    }
}