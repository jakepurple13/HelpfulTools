package com.programmersbox.gsonutils

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    data class AnotherObject(val item: String)

    data class GsonObject(val string: String, val int: Int, val anotherObject: AnotherObject)

    @Test
    fun gsonTest() {
        val item = GsonObject("Hello", 5, AnotherObject("World"))
        println(item)
        println(item.toJson())
        println(item.toPrettyJson())
        val json = item.toJson()
        println(json.fromJson<GsonObject>())
        assertEquals("These should be equal", item, json.fromJson<GsonObject>())
    }
}