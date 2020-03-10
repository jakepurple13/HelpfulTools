package com.programmersbox.helpfultools

import com.programmersbox.flowutils.FlowItem
import com.programmersbox.gsonutils.getJsonApi
import com.programmersbox.helpfulutils.stringForTime
import com.programmersbox.helpfulutils.toHexString
import com.programmersbox.loggingutils.FrameType
import com.programmersbox.loggingutils.Loged
import com.programmersbox.loggingutils.f
import com.programmersbox.loggingutils.frame
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
    fun colorApi() {
        val f = getJsonApi<ColorApi>("http://thecolorapi.com/id?hex=${0x0450505.toHexString().drop(1)}")
        println(f)
    }

    @Test
    fun stringTimeTest() {
        Loged.f(100000L.stringForTime())
        Loged.f(FlowItem(100).toString())
        println(Names.names.size)
        val group = Names.names.groupBy { it[0] }
        group.entries.forEach {
            println(it.key)
            println(it.value.frame(FrameType.BOX.copy(top = "Letter: ${it.key}")))
        }
    }
}