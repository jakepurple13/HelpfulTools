package com.programmersbox.loggingutils

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
        //In testing, everything ends up being printed via the normal println method
        Loged.UNIT_TESTING = true
        Loged.TAG = "LoggingTest"
        Loged.FILTER_BY_PACKAGE_NAME = "programmersbox"
        Loged.SHOW_PRETTY = true
        Loged.WITH_THREAD_NAME = true
        Loged.OTHER_CLASS_FILTER = { true }
        Loged.OTHER_CLASS_FILTER { true }
        Loged.defaultFrameType = FrameType.values().random()
    }

    @Test
    fun methodChain() {
        fun a() = Loged.i("We are here")
        fun b() = a()
        fun c() = b()
        c()
    }

    private fun newLine(category: String = "") = println("$category${"-".repeat(50)}")

    @Test
    fun logedTest() {
        //These will do normal logs
        newLine("Normal")
        Loged.w("Hello World")
        Loged.a("Hello World")
        Loged.i("Hello World")
        Loged.v("Hello World")
        Loged.e("Hello World")
        Loged.d("Hello World")
        Loged.wtf("Hello World")
        Loged.r("Hello World")
        //These will put a box around the log
        newLine("Boxed")
        Loged.f("Hello World")
        Loged.fw("Hello World")
        Loged.fa("Hello World")
        Loged.fi("Hello World")
        Loged.fv("Hello World")
        Loged.fe("Hello World")
        Loged.fd("Hello World")
    }

    @Test
    fun listLogedTest() {
        val list = listOf("Hello", "World")
        Loged.f(list)
        Loged.fw(list)
        Loged.fa(list)
        Loged.fi(list)
        Loged.fv(list)
        Loged.fe(list)
        Loged.fd(list)
    }

    @Test
    fun transformLogedTest() {
        data class Test(val item: String)

        val list = listOf(Test("Hello World"), Test("We are here"))
        Loged.f(list) { it.item }
        Loged.fw(list) { it.item }
        Loged.fa(list) { it.item }
        Loged.fi(list) { it.item }
        Loged.fv(list) { it.item }
        Loged.fe(list) { it.item }
        Loged.fd(list) { it.item }
    }

    @Test
    fun objectLogedTest() {
        data class Test(val item: String)

        val list = Test("Hello World")
        Loged.f(list)
        Loged.fw(list)
        Loged.fa(list)
        Loged.fi(list)
        Loged.fv(list)
        Loged.fe(list)
        Loged.fd(list)
    }

    @Test
    fun fullParamInsertedTest() {
        //These will do normal logs
        newLine("Normal")
        Loged.w("Hello World", "Tag", showPretty = false, threadName = false)
        Loged.a("Hello World", "Tag", showPretty = false, threadName = false)
        Loged.i("Hello World", "Tag", showPretty = false, threadName = false)
        Loged.v("Hello World", "Tag", showPretty = false, threadName = false)
        Loged.e("Hello World", "Tag", showPretty = false, threadName = false)
        Loged.d("Hello World", "Tag", showPretty = false, threadName = false)
        Loged.wtf("Hello World", "Tag", showPretty = false, threadName = false)
        Loged.r("Hello World", "Tag", showPretty = false, threadName = false, choices = *intArrayOf(2, 3, 4, 5, 6, 7))
        //These will put a box around the log
        newLine("Boxed")
        Loged.f(
            "Hello World",
            "Tag",
            "Info Text",
            showPretty = false,
            threadName = false,
            frameType = FrameType.BOXED.copy(bottomLeft = "╠"),
            choices = *intArrayOf(2, 3, 4, 5, 6, 7)
        )
        Loged.fw("Hello World", "Tag", "Info Text", showPretty = false, threadName = false, frameType = FrameType.BOXED.copy(bottomLeft = "╠"))
        Loged.fa("Hello World", "Tag", "Info Text", showPretty = false, threadName = false, frameType = FrameType.BOXED.copy(bottomLeft = "╠"))
        Loged.fi("Hello World", "Tag", "Info Text", showPretty = false, threadName = false, frameType = FrameType.BOXED.copy(bottomLeft = "╠"))
        Loged.fv("Hello World", "Tag", "Info Text", showPretty = false, threadName = false, frameType = FrameType.BOXED.copy(bottomLeft = "╠"))
        Loged.fe("Hello World", "Tag", "Info Text", showPretty = false, threadName = false, frameType = FrameType.BOXED.copy(bottomLeft = "╠"))
        Loged.fd("Hello World", "Tag", "Info Text", showPretty = false, threadName = false, frameType = FrameType.BOXED.copy(bottomLeft = "╠"))
    }

    @Test
    fun frameTypeTest() {
        //Strings will have the entire text on one line in a frame
        FrameType.values().forEach { println("Hello World ${it.name}".frame(it)) }
    }

    @Test
    fun listFrame() {
        //Lists will have each element on a new line
        FrameType.values().forEach { println(listOf("Hello", "World", it.name).frame(it)) }
    }

    @Test
    fun frames() {
        FrameType.BOXED
        FrameType.BOX
        FrameType.ASTERISK
        FrameType.DIAGONAL
        FrameType.OVAL
        FrameType.PLUS
        FrameType.CUSTOM
        val custom = FrameType.CUSTOM {
            top = "top"
            bottom = "bottom"
            left = "left"
            right = "right"
            topLeft = "topLeft"
            topRight = "topRight"
            bottomLeft = "bottomLeft"
            bottomRight = "bottomRight"
            topFillIn = "topFillIn"
            bottomFillIn = "bottomFillIn"
        }
        println("Hello".frame(custom))
        val boxCustom = FrameType.CUSTOM.copy(FrameType.BOX.frame)
        println("Hello".frame(boxCustom))
    }
}