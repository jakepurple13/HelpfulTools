package com.programmersbox.funutils

import android.view.View
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.programmersbox.funutils.funutilities.TimedSequenceMaker
import com.programmersbox.funutils.views.setOnFingerDetector
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
        assertEquals("com.programmersbox.funutils.test", appContext.packageName)

        TimedSequenceMaker(1, 2, 3, 400L) {

        }

        View(appContext).setOnFingerDetector(4) { fingers, fingerIndex ->

        }
    }
}