package com.programmersbox.flowutils

import android.view.View
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.runBlocking
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
    private val flowItem = FlowItem(5)
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.programmersbox.flowutils.test", appContext.packageName)
    }

    @Test
    fun flowTest() = runBlocking {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val view = View(appContext)
        flowItem.bindToUI(view) { println(it) }
        flowItem.collectOnUI { println(it) }
        flowItem.flow.collectOnUi { println(it) }
    }.let { Unit }
}