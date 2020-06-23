package com.programmersbox.gsonutils

import com.google.gson.JsonObject
import com.google.gson.JsonSerializer
import com.programmersbox.helpfulutils.*
import org.json.JSONObject
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
    fun jsonTest3() {
        val f = FixedListTwo<Int>(5, FixedListLocation.END)
        f.addAll(1, 2, 3, 4, 5)
        println(f)
        println(f.toJson())
        val f1 = f.toJson().fromJson<FixedListTwo<Int>>()
        println(f1)

        println(f.toJson2())
    }

    data class FixedListTwo<T>(
        private var internalFixedSize: Int,
        /**
         * choose where to remove items from
         * Default is [FixedListLocation.END]
         */
        var removeFrom: FixedListLocation = FixedListLocation.END,
        private val itemList: MutableList<T> = mutableListOf()
    ) : MutableList<T> by itemList {

        fun toJson() = JsonObject().apply {
            addProperty("fixedSize", internalFixedSize)
            addProperty("removeFrom", removeFrom.toJson())
            addProperty("itemList", itemList.toJson())
        }.toJson()

        fun toJson2() = JSONObject().apply {
            put("fixedSize", internalFixedSize)
            put("removeFrom", removeFrom.toJson())
            put("itemList", itemList.toJson())
        }.toJson()

        var fixedSize: Int
            get() = internalFixedSize
            set(value) {
                require(value > 0) { "FixedSize must be greater than 0" }
                internalFixedSize = value
                multipleSizeCheck()
            }

        private val removeIndex
            get() = when (removeFrom) {
                FixedListLocation.START -> 0
                FixedListLocation.END -> lastIndex
            }

        protected fun singleSizeCheck() {
            if (size > fixedSize) removeAt(removeIndex)
        }

        override fun add(element: T): Boolean {
            val addition = itemList.add(element)
            singleSizeCheck()
            return addition
        }

        override fun add(index: Int, element: T) {
            val addition = itemList.add(index, element)
            singleSizeCheck()
            return addition
        }

        protected fun multipleSizeCheck() {
            while (size > fixedSize) removeAt(removeIndex)
        }

        override fun addAll(elements: Collection<T>): Boolean {
            val addition = itemList.addAll(elements)
            multipleSizeCheck()
            return addition
        }

        override fun addAll(index: Int, elements: Collection<T>): Boolean {
            val addition = itemList.addAll(index, elements)
            multipleSizeCheck()
            return addition
        }
    }

    @Test
    fun jsonTest2() {
        //val f2 = FixedList(fixedSize = 5, removeFrom = FixedListLocation.END, c = listOf(1, 2, 3))
        println("List")
        val f = fixedListOf(5, 1, 2, 3, 4, 5)
        f.removeFrom = FixedListLocation.START
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

        println("Set")
        val set = fixedSetOf(5, 1, 2, 3, 4, 4, 5)
        println(set)
        val set1 = set.toHelpfulJson()
        println(set1)
        val set2 = set1.fromJsonToHelpful<FixedSet<Int>>()
        println(set2)
        set2?.forEach { println(it) }

        println("Map")
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

    @Test
    fun itemRangeTest() {
        println("ItemRange")
        val f = itemRangeOf(1, 2, 3)
        f.current = 1
        println(f)
        val f1 = f.toHelpfulJson()
        println(f1)
        val f2 = f1.fromJsonToHelpful<ItemRange<Int>>()
        println(f2)

        println("MutableItemRange")
        val m = mutableItemRangeOf(1, 2, 3)
        m.current = 1
        println(m)
        val m1 = m.toHelpfulJson()
        println(m1)
        val m2 = m1.fromJsonToHelpful<MutableItemRange<Int>>()
        println(m2)

        println("NumberRange")
        val n = NumberRange(1..5 step 2)
        println(n)
        val n1 = n.toJson()
        println(n1)
        val n2 = n1.fromJson<NumberRange>()
        println(n2)
    }

    @Test
    fun rangingTest() {
        val f = FixedRange(1, 2, 3)
        println(f)
        val f1 = f.toJson()
        println(f1)
        val f2 = f1.fromJson<FixedRange<Int>>()
        println(f2)
    }

    abstract class Ranging<T>(
        val itemList: MutableCollection<T>,
        var fixedSize: Int = 5,
        var removeFrom: FixedListLocation = FixedListLocation.END
    ) : MutableCollection<T> by itemList {

    }

    class FixedRange<T>(itemList: MutableList<T>) : Ranging<T>(itemList) {
        constructor(vararg items: T) : this(items.toMutableList())
    }
}