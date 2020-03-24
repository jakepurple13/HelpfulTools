package com.programmersbox.testingplayground

import android.graphics.Color
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
        assertEquals("com.programmersbox.testingplayground.test", appContext.packageName)

    }

    @Test
    fun other7() {
        val list = listOf(
            "#FAFAFA",
            "#FAFAFA",
            "#000000",
            "#de605e",
            "#F2ACAC",
            "#C16363",
            "#FAFAFA",
            "#B61F1E",
            "#951A18",
            "#CC2523",
            "#ffd5d5",
            "#FFCECE",
            "#FFE1E1",
            "#FFD5D5",
            "#D64542",
            "#8E3230",
            "#D6403D",
            "#DB3D38",
            "#FFC1BF",
            "#D58582"
        ).shuffled()

        list.forEach { println(it.color(Color.parseColor(it))) }

        val f = list.sortedBy {
            val array = floatArrayOf(0f, 0f, 0f)
            Color.colorToHSV(Color.parseColor(it), array)
            array[0]
        }

        f.forEach { println(it.color(Color.parseColor(it))) }
    }
}