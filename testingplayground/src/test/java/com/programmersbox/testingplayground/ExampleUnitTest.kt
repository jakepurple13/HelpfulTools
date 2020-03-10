package com.programmersbox.testingplayground

import com.programmersbox.helpfulutils.intersect
import com.programmersbox.helpfulutils.randomString
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
        Loged.FILTER_BY_PACKAGE_NAME = "programmersbox"
    }

    data class TestClass(val s: String = 10.randomString(), val num: Int = 5)

    @Test
    fun other() {
        val list = listOf("Hello", "World")
        val list2 = listOf("Goodbye", "World")

        Loged.f(list.union(list2))
        Loged.f(list.intersect(list2) { s, s1 -> s == s1 })
        Loged.f(list.intersect(list2))
    }

    @Test
    fun other2() {
        val list = listOf(TestClass(), TestClass())
        val list2 = listOf(TestClass(), TestClass())

        Loged.f(list.union(list2))
        Loged.f(list.intersect(list2) { s, s1 -> s.num == s1.num })
        Loged.f(list.intersect(list2))
        Loged.f(list.fold(0) { t, t1 -> t + t1.num })
        Loged.f(list.sumBy { it.num })
        val first = Array(2) { x -> Array(2) { y -> x + y } }
        val second = Array(2) { x -> Array(2) { y -> x * y } }
        val third = first + second
        println(first.joinedToString())
        println()
        println(second.joinedToString())
        println()
        println(third.joinedToString())
    }

    private fun Array<Array<Int>>.joinedToString() = joinToString("\n") { it.joinToString() }

    operator fun Array<Array<Int>>.plus(other: Array<Array<Int>>) = Array(size) { x -> Array(get(x).size) { y -> get(x)[y] + other[x][y] } }
    operator fun Array<Array<Int>>.times(other: Array<Array<Int>>) = Array(size) { x -> Array(get(x).size) { y -> get(x)[y] * other[x][y] } }
    operator fun Array<Array<Int>>.minus(other: Array<Array<Int>>) = Array(size) { x -> Array(get(x).size) { y -> get(x)[y] - other[x][y] } }
    operator fun Array<Array<Int>>.div(other: Array<Array<Int>>) = Array(size) { x -> Array(get(x).size) { y -> get(x)[y] / other[x][y] } }

    @Test
    fun other3() {
        val list = listOf(TestClass("Hello"), TestClass("World"))
        println(
            list
                .frame(FrameType.PLUS)
                .frame(FrameType.ASTERISK)
                .frame(FrameType.DIAGONAL)
                .frame(FrameType.OVAL)
                .frame(FrameType.BOXED)
                .frame(FrameType.BOX)
        )

        println(boxFort())
        println(boxFort(3, "Hello"))
        println()
        FrameType.values().forEach { println(it.name.frame(it)) }
    }

    private fun boxFort(num: Int = 1, text: String = ""): String {
        val types = listOf(FrameType.PLUS, FrameType.ASTERISK, FrameType.DIAGONAL, FrameType.OVAL, FrameType.BOXED, FrameType.BOX)
        var listString = text
        repeat(num) { types.forEach { listString = listString.frame(it) } }
        return listString
    }

}