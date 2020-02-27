package com.programmersbox.helpfultools

import com.programmersbox.flowutils.FlowItem
import com.programmersbox.helpfulutils.stringForTime
import com.programmersbox.loggingutils.Loged
import com.programmersbox.loggingutils.f
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
        Loged.FILTER_BY_PACKAGE_NAME = "com.programmersbox.helpfultools"
    }

    @Test
    fun stringTimeTest() {
        Loged.f(100000L.stringForTime())
        Loged.f(FlowItem(100).toString())
    }
}