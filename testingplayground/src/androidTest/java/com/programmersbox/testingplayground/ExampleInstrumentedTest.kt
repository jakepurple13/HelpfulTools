package com.programmersbox.testingplayground

import android.view.View
import android.widget.Button
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
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
        assertEquals("com.programmersbox.testingplayground.test", appContext.packageName)
        val b = Button(appContext)
        b.clicks()
        b.longClicks()

    }

    fun View.clicks(): Flow<Unit> {
        val channel = BroadcastChannel<Unit>(1)
        setOnClickListener { channel.offer(Unit) }
        return channel.asFlow()
    }

    fun View.longClicks(): Flow<Unit> {
        val channel = BroadcastChannel<Unit>(1)
        setOnLongClickListener { channel.offer(Unit) }
        return channel.asFlow()
    }
}