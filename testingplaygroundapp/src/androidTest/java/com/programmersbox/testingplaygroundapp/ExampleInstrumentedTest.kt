package com.programmersbox.testingplaygroundapp

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.programmersbox.helpfulutils.defaultSharedPref
import com.programmersbox.helpfulutils.get
import com.programmersbox.helpfulutils.sharedPrefDelegate
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

    var Context.asdf: Int? by InstrumentationRegistry.getInstrumentation().targetContext.sharedPrefDelegate()

    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.programmersbox.testingplaygroundapp", appContext.packageName)
        with(appContext) {
            var f: String? by sharedPrefDelegate("0")

            f = "5"
            println(f)
            println(defaultSharedPref.all.entries)

            println(defaultSharedPref.get("f", "9"))

            var a: Int? by sharedPrefDelegate(9, "thisIsTheKey")
            a = 54
            println("$a")

            var x: Int? = 5
            x = 9
            println(x)

        }

    }

}