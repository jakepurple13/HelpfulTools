package com.programmersbox.helpfulutils

import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.random.Random

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    data class UtilObject(var string: String, var int: Int?)

    @Test
    fun whatIfTest() {
        val intValue = if (Random.nextBoolean()) Random.nextInt(0, 20) else null
        val util = UtilObject("Hello", intValue)
        println(util)

        util
            .whatIf(util.int in 0..5) { string = "World" }
            .whatIf(util.int in 5..10, whatIfTrue = { string = "Goodbye" }, whatIfFalse = { string = "World" })
            .whatIfNotNull(util.int) { string = "Its not null $it" }

        println(util)
    }

    @Test
    fun mutableTest() {
        val list = mutableListOf<Int>()
        list.addAll(1, 3, 5, 6, 7, 8)
        println(list)
        println(list.randomRemove())
        println(list)
        println(list.randomRemove { it % 2 == 1 })
        println(list)
        println(list.random { it % 2 == 0 })
        val list2 = listOf(1, 2, 4, 8)
        val list3 = list.intersect(list2) { l1, l2 -> l1 == l2 }
        println(list3)
        val pair = list to list2
        println(pair.intersect { i, i2 -> i == i2 })
        println(sizedListOf(5) { Random.nextString(5) })
        println(list)
        println(list.lastWithIndex)
    }

    @Test
    fun stringForTimeTest() {
        //Long
        println(360000L.stringForTime())
        assertEquals("360000L should be 06:00", "06:00", 360000L.stringForTime())
        //Int
        println(360000.stringForTime())
        assertEquals("360000 should be 06:00", "06:00", 360000.stringForTime())
    }

    @Test
    fun numberTest() {
        println(30.randomString())
        println(30.randomString().length)
        println(Random.nextString(30))
        println(Random.nextString(30).length)
        println(0x0000FF.toHexString())
        println(0x0000FF.toARGB())
        println(0x0000FF.toRGB())
        println(0x0000FF.toRGB().toInt())
        println(0x0000FF)
        println(0x0000FF.toARGB().toInt())
        println(0x0000FF.toCMYK().toInt())
        println(0x0000FF.toCMYK())
        println(0x0000FF.toARGB().toRGB())
        println(0x0000FF.toRGB().toARGB())
        println(0x0000FF.toCMYK().toRGB())
        println(0x0000FF.toARGB().toCMYK())
        println(0x0000FF.toRGB().toCMYK())
        println(0x0000FF.toCMYK().toARGB())
    }

    @Test
    fun rangeTest() {
        println("Item Loop = true --------")
        var f = ItemRange(1, 2, 3, 4, 5)
        for (i in 0..10) {
            println(f())
            f++
        }
        println("-----")
        for (i in 0..10) {
            println(f())
            f--
        }
        println("Number Loop = true --------")
        var n = NumberRange(1..5)
        for (i in 0..10) {
            println(n())
            n++
        }
        println("-----")
        for (i in 0..10) {
            println(n())
            n--
        }
        println("Item Loop = false--------")
        var f1 = ItemRange(1, 2, 3, 4, 5, loop = false)
        for (i in 0..10) {
            println(f1())
            f1++
        }
        println("-----")
        for (i in 0..10) {
            println(f1())
            f1--
        }
        println("Number Loop = false--------")
        var n1 = NumberRange(1..5, false)
        for (i in 0..10) {
            println(n1())
            n1++
        }
        println("-----")
        for (i in 0..10) {
            println(n1())
            n1--
        }
    }

}