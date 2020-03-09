package com.programmersbox.rxutils

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import io.reactivex.subjects.PublishSubject
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
        assertEquals("com.programmersbox.rxutils.test", appContext.packageName)
        val publish = PublishSubject.create<String>()
        publish
            .ioMain()
            .doOnError { println(it) }
            .doOnComplete { println("Done") }
            .subscribe { println(it) }
        publish("Hello")
        publish.onNext("World")
        publish()
        publish(Throwable("Error!"))
    }
}