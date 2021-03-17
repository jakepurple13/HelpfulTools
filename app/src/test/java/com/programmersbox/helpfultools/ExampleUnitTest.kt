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
import kotlin.math.cos
import kotlin.system.measureNanoTime

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

    fun <In, Out> cached2(f: (In) -> Out): (In) -> Out = mutableMapOf<In, Out>().let { { `in`: In -> it.computeIfAbsent(`in`, f) } }

    fun <In, Out> cached(f: (In) -> Out): (In) -> Out {
        val cache = mutableMapOf<In, Out>()
        return { input: In -> cache.computeIfAbsent(input, f) }
    }

    @Test
    fun pureFunction() {
        val cachedCos = cached { x: Double -> cos(x) }

        println(measureNanoTime { cachedCos(Math.PI * 2) }) // 329378 ns

        /* value of cos for 2π is now cached */

        println(measureNanoTime { cachedCos(Math.PI * 2) }) // 6286 ns

        val cachedCos2 = cached2 { x: Double -> cos(x) }

        println(measureNanoTime { cachedCos2(Math.PI * 2) }) // 329378 ns

        /* value of cos for 2π is now cached */

        println(measureNanoTime { cachedCos2(Math.PI * 2) }) // 6286 ns
    }

}