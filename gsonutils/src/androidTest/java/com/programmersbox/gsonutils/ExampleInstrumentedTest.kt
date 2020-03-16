package com.programmersbox.gsonutils

import android.content.Context
import android.content.Intent
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

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
        assertEquals("com.programmersbox.gsonutils.test", appContext.packageName)
    }

    data class AnotherObject(val item: String)

    data class GsonObject(var string: String, val int: Int, val anotherObject: AnotherObject)

    @Test
    fun gsonTest() {
        val item = GsonObject("Hello", 5, AnotherObject("World"))
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        Intent(appContext, ExampleInstrumentedTest::class.java).apply {
            putExtra("gson", item)
        }.apply {
            val getting = getObjectExtra<GsonObject?>("gson", null)
            println(getting)
        }

        appContext.getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE).edit().putObject("gson", item).apply()
        val getting = appContext.getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE).getObject<GsonObject>("gson")
        println(getting)
        appContext.getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE).updateObject<GsonObject>("gson") { string = "Hello" }
        val getting1 = appContext.getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE).getObject<GsonObject>("gson")
        println(getting1)
    }
}