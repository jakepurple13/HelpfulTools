package com.programmersbox.flowutils

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    @Test
    fun flowTest() {
        println(FlowItem(100))
    }

    val item: FlowItem<Int> = 1.asFlowItem()

    @Test
    fun flowItemTest() = runBlocking {
        item.collect { println(it) }
        delay(1000)
        item(10)
        item.setValue(100)
        delay(1000)
        item.now()
        GlobalScope.launch { item.flow.collect { println("From item.flow $it") } }
        item(20)
        delay(1000)
        item.setValue(60)
        delay(1000)
    }

    @Test
    fun tickerTest() = runBlocking {
        timerFlow(500) { println("Time passed") }
        delay(2500)
    }

    private val booleanItem: FlowItem<Boolean> = true.asFlowItem()
    private val intItem: FlowItem<Int> = 1.asFlowItem()
    private val longItem: FlowItem<Long> = 1L.asFlowItem()
    private val doubleItem: FlowItem<Double> = 1.0.asFlowItem()
    private val floatItem: FlowItem<Float> = 1F.asFlowItem()
    private val shortItem: FlowItem<Short> = 1.toShort().asFlowItem()

    private fun newLine(category: String = "") = println("$category${"-".repeat(50)}")

    @Test
    fun flowItemOperatorTest() = runBlocking {
        booleanItem.collect { println("Boolean: $it") }
        intItem.collect { println("Int $it") }
        longItem.collect { println("Long $it") }
        doubleItem.collect { println("Double $it") }
        floatItem.collect { println("Float $it") }
        shortItem.collect { println("Short $it") }
        delay(1000)
        !booleanItem
        newLine("Plus")
        intItem += 5
        longItem += 5
        doubleItem += 5.0
        floatItem += 5F
        shortItem += 5.toShort()
        delay(1000)
        newLine("Times")
        intItem *= 5
        longItem *= 5
        doubleItem *= 5.0
        floatItem *= 5F
        shortItem *= 5.toShort()
        delay(1000)
        newLine("Minus")
        intItem -= 5
        longItem -= 5
        doubleItem -= 5.0
        floatItem -= 5F
        shortItem -= 5.toShort()
        delay(1000)
        newLine("Divide")
        intItem /= 5
        longItem /= 5
        doubleItem /= 5.0
        floatItem /= 5F
        shortItem /= 5.toShort()
        delay(1000)
        newLine("Mod")
        intItem %= 5
        longItem %= 5
        doubleItem %= 5.0
        floatItem %= 5F
        shortItem %= 5.toShort()
        delay(1000)
    }

    private val mutableListItem = FlowItem(mutableListOf(1, 2, 3, 4, 5))

    @Test
    fun flowItemListTest() = runBlocking {
        newLine("List")
        val listItem = FlowItem(listOf(1, 2, 3, 4, 5))
        println(listItem[3])
        newLine("Mutable")
        mutableListItem.collect { println("MutableList(${mutableListItem.size}): $it") }
        delay(1000)
        println(mutableListItem[2])
        mutableListItem[2] = 10
        println(mutableListItem[2])
        delay(1000)
        for (i in mutableListItem) println(i)
        delay(1000)
        println(10 in mutableListItem)
        delay(1000)
        mutableListItem += 50
        delay(1000)
        mutableListItem.add(20)
        delay(1000)
    }

}