package com.programmersbox.gsonutils

import com.google.gson.JsonObject
import com.google.gson.JsonSerializer
import com.programmersbox.helpfulutils.*
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

        //I know this is redundant but its just for an example
        val toJsonAdapter =
            item.toJson(AnotherObject::class.java to JsonSerializer<AnotherObject?> { a, _, _ ->
                JsonObject().apply { addProperty("anotherObject", a.toJson()) }
            })

        println(toJsonAdapter)
    }

    @Test
    fun setTest() {
        val set = fixedSetOf(5, 1, 2, 3, 4, 4, 5)
        println(set)
        val set1 = set.toHelpfulJson()
        println(set1)
        val set2 = set1.fromJsonToHelpful<FixedSet<Int>>()
        println(set2)
    }

    @Test
    fun itemRangeTest() {
        val f = itemRangeOf(1, 2, 3)
        f.current = 1
        println(f)
        val f1 = f.toHelpfulJson()
        println(f1)
        val f2 = f1.fromJsonToHelpful<ItemRange<Int>>()
        println(f2)

        val m = mutableItemRangeOf(1, 2, 3)
        m.current = 1
        println(m)
        val m1 = m.toHelpfulJson()
        println(m1)
        val m2 = m1.fromJsonToHelpful<MutableItemRange<Int>>()
        println(m2)
    }

    @Test
    fun jsonTest2() {
        //val f2 = FixedList(fixedSize = 5, removeFrom = FixedListLocation.END, c = listOf(1, 2, 3))

        val f = fixedListOf(5, 1, 2, 3, 4, 5)
        println(f)
        val s = f.toHelpfulJson()
        println(s)
        println("Now from json")
        val f1 = s.fromJsonToHelpful<FixedList<Int>>()
        println(f1)

        println()
        println(f1?.toMutableList())
        println()

        f1?.forEach { println(it) }
        f1?.forEach { println(it is Int) }
        println(f1)

        val set = fixedSetOf(5, 1, 2, 3, 4, 4, 5)
        println(set)
        val set1 = set.toHelpfulJson()
        println(set1)
        val set2 = set1.fromJsonToHelpful<FixedSet<Int>>()
        println(set2)
        set2?.forEach { println(it) }

        val m = fixedMapOf(5, "hello" to 1, "world" to 2)
        println(m)
        val ms = m.toHelpfulJson()
        println(ms)
        val m1 = ms.fromJsonToHelpful<FixedMap<String, Double>>()
        println(m1)
        println(m1?.get("hello"))

        println("-".repeat(50))

        itemRangeTest()

    }
}