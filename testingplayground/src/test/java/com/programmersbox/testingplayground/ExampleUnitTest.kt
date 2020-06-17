package com.programmersbox.testingplayground

import com.programmersbox.funutils.cards.Card
import com.programmersbox.funutils.cards.Deck
import com.programmersbox.helpfulutils.FixedList
import com.programmersbox.helpfulutils.intersect
import com.programmersbox.helpfulutils.randomString
import com.programmersbox.loggingutils.FrameType
import com.programmersbox.loggingutils.Loged
import com.programmersbox.loggingutils.f
import com.programmersbox.loggingutils.frame
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
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

    @Test
    fun other4() {
        println("#ff0fa10e".color(0xff0fa10e))
    }

    private fun String.vowelsUpConsonantsDown() = map { if ("aeiou".contains(it, true)) it.toUpperCase() else it.toLowerCase() }.joinToString("")

    @Test
    fun other8() {
        val h = "Hello World"
        println(h.vowelsUpConsonantsDown())
        println(0x80)
    }

    @Test
    fun other9() {
        val deck = Deck.defaultDeck()
        Loged.f(deck.deck)
    }

    @Test
    fun other10() = runBlocking {
        println("Interface---")
        val deck = Deck.defaultDeck()
        deck.addDeckListener {
            onAdd { println(it.map(Card::toSymbolString)) }
            onDraw { card, size -> println("$card and $size") }
            onShuffle { println("Shuffled") }
        }
        deck.draw()
        deck.add(Card.RandomCard)
        deck.shuffle()
        println("Rx---")
        val rxDeck = RxDeck.defaultDeck()
        rxDeck.onAddSubscribe()
            .map { it.map { it.toSymbolString() } }
            .subscribe { println(it) }
        rxDeck.onDrawSubscribe()
            .subscribe { println("${it.card} and ${it.size}") }
        rxDeck.onShuffleSubscribe()
            .subscribe { println("Shuffled") }
        rxDeck.draw()
        rxDeck.add(Card.RandomCard)
        rxDeck.shuffle()
        rxDeck(Deck.defaultDeck())
        println("Flow---")
        val flowDeck = FlowDeck.defaultDeck()
        GlobalScope.launch {
            flowDeck.onAddCollect()
                .map { it.map { it.toSymbolString() } }
                .collect { println(it) }
        }
        delay(50)
        GlobalScope.launch {
            flowDeck.onDrawCollect()
                .collect { println("${it.card} and ${it.size}") }
        }
        delay(50)
        GlobalScope.launch {
            flowDeck.onShuffleCollect()
                .collect { println("Shuffled") }
        }
        delay(50)
        flowDeck.draw()
        flowDeck.add(Card.RandomCard)
        flowDeck.shuffle()
        delay(500)
        println("Equals Checking---")
        val deck2 = Deck.defaultDeck()
        val rxDeck2 = RxDeck.defaultDeck()
        val flowDeck2 = FlowDeck.defaultDeck()
        println(deck2 == rxDeck2)
        println(deck2 == flowDeck2)
        println(rxDeck2 == flowDeck2)
    }

    @Test
    fun other11() {
        val deck = RxDeck.defaultDeck()
        deck.onAddSubscribe()
            .map { it.map { it.toSymbolString() } }
            .subscribe { println(it) }
        deck.onDrawSubscribe()
            .subscribe {
                if (it.size <= 5) deck(Deck.defaultDeck())
                println("${it.card} and ${it.size}")
            }
        deck.onShuffleSubscribe()
            .subscribe { println("Shuffled") }
        deck.trueRandomShuffle()
        val hand = deck.draw(5)
        println(hand.map(Card::toSymbolString))
        deck.draw(50)
        println(deck)
    }

    @Test
    fun other12() {
        println(3.noPlus(4))
    }

    fun Int.noPlus(other: Int): Int {
        var x = this
        var y = other
        while (y != 0) {
            val carry = x and y
            x = x xor y
            y = carry shl 1
        }
        return x
    }

    private val randomColor get() = (Math.random() * 16777215).toInt() or (0xFF shl 24)

    @Test
    fun other6() {
        val list = mutableListOf<Int>().apply { repeat(100) { this += randomColor } }
        list.sortedBy { rgbToHsl(it)[0] }.forEach { println("Hello World".color(it)) }
    }

    private fun rgbToHsl(color: Int): Array<Float> {
        val (r, g, b) = color.valueOf()
        val max = maxOf(r, g, b)
        val min = minOf(r, g, b)
        var h: Float
        val s: Float
        val l = (max + min) / 2f
        if (max == min) {
            s = 0f
            h = s
        } else {
            val d = max - min
            s = if (l > 0.5) d / (2 - max - min).toFloat() else d / (max + min).toFloat()
            h = when (max) {
                r -> (g - b) / d + (if (g < b) 6f else 0f)
                g -> (b - r) / d + 2f
                b -> (r - g) / d + 4f
                else -> 0f
            }
            h /= 6f
        }
        return arrayOf(h * 360f, s * 100f, l * 100f)
    }

    private fun Int.valueOf(): Triple<Int, Int, Int> {
        val r = (this shr 16 and 0xff)// / 255.0f
        val g = (this shr 8 and 0xff)// / 255.0f
        val b = (this and 0xff)// / 255.0f
        return Triple(r, g, b)
    }

    @Test
    fun other5() {
        println(4 or 5 or 6 or 7 or 8 or 9)
        val f = listOf(4, 5, 6, 7, 8, 9)
        println(f.fold(0) { a, c -> a or c })
        val f1 = FixedList<Int>(50)
        for (i in 0..100) {
            f1.add(i)
        }
        println(f1)
        println(f1.size)
    }

}

class FixedSizeList<T>(maxSize: Int = 1) : FixedList<T>(maxSize) {
    override fun add(element: T): Boolean = super.add(element).also { singleSizeCheck() }
    override fun add(index: Int, element: T) = super.add(index, element).also { singleSizeCheck() }
    override fun addAll(elements: Collection<T>): Boolean = super.addAll(elements).also { multipleSizeCheck() }
    override fun addAll(index: Int, elements: Collection<T>): Boolean = super.addAll(index, elements).also { multipleSizeCheck() }
}