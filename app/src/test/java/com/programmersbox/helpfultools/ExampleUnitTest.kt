package com.programmersbox.helpfultools

import com.programmersbox.loggingutils.Loged
import org.junit.Before
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    @Before
    fun setup() {
        Loged.UNIT_TESTING = true
        Loged.FILTER_BY_CLASS_NAME = "com.programmersbox.helpfultools"
    }

    @Test
    fun addition_isCorrect() {
        //Loged.f(100000L.stringForTime())
    }
}