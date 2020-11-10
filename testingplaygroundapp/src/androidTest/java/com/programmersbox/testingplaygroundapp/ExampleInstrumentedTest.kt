package com.programmersbox.testingplaygroundapp

import android.content.Context
import androidx.core.app.TaskStackBuilder
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.programmersbox.gsonutils.toJson
import com.programmersbox.helpfulutils.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
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

    private val appContext = InstrumentationRegistry.getInstrumentation().targetContext

    private var Context.asdf: String? by sharedPrefDelegate("")
    private var Context.a: Int? by sharedPrefDelegate(9, "thisIsTheKey")
    private var Context.abc: Int by sharedPrefNotNullDelegate(9, "thisIsTheKey")

    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.programmersbox.testingplaygroundapp", appContext.packageName)
        with(appContext) {
            //var f: String? by sharedPrefDelegate("0")

            asdf = "5"
            println(asdf)
            println(defaultSharedPref.all.entries)

            println(defaultSharedPref.get("asdf", "9"))

            a = 54
            println("$a")

            val z: String? = defaultSharedPref["asdf"]
            println(z)

            defaultSharedPref["asdf"] = "Hello World"
        }

    }

    @Test
    fun nextTry() {
        appContext.powerManager.addThermalStatusListener { }

        GlobalScope.launch {

        }
    }

    @Test
    fun gsonTaskTest() {
        val f = TaskStackBuilder.create(appContext)
            //.addNextIntent(Intent(appContext, MainActivity::class.java))
            .toJson()
        println(f)
    }

}