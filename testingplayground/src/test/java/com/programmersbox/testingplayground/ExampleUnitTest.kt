package com.programmersbox.testingplayground

import com.programmersbox.funutils.cards.AbstractDeck
import com.programmersbox.funutils.cards.Card
import com.programmersbox.funutils.cards.Deck
import com.programmersbox.gsonutils.fromJson
import com.programmersbox.gsonutils.toJson
import com.programmersbox.helpfulutils.*
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
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Before
import org.junit.Test
import kotlin.random.Random


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

    private fun Random.nextColor(
        red: Int = nextInt(0, 255),
        green: Int = nextInt(0, 255),
        blue: Int = nextInt(0, 255)
    ): Int {
        //ARGB(a=133, r=198, g=134, b=34) works
        //ARGB(a=0, r=163, g=36, b=194)
        return Colors.RGB(red, green, blue).also { println(it) }.toInt()
    }

    @Serializable
    data class SerializeTest(val r: Int, val g: Int, val b: Int)

    inline fun <reified T> T.toJsonX(json: Json = Json) = json.encodeToString(this)
    inline fun <reified T> T.toPrettyJsonX() = Json { prettyPrint = true }.encodeToString(this)
    inline fun <reified T> String.fromJsonX(json: Json = Json) = json.decodeFromString<T>(this)

    @Test
    fun serializableTesting() {

        run {
            val s = SerializeTest(25, 64, 128)

            println(s)

            val f = Json.encodeToString(s)

            println(f)

            val s1 = Json.decodeFromString<SerializeTest>(f)

            println(s1)
        }

        println("-".repeat(50))

        run {
            val s = SerializeTest(25, 64, 128)

            println(s)

            val f = s.toJson()

            println(f)

            val s1 = f.fromJson<SerializeTest>()

            println(s1)
        }

        println("-".repeat(50))

        run {

            val format = Json { encodeDefaults = true }

            @Serializable
            data class Project(
                val name: String,
                val language: String = "Kotlin",
                val website: String? = null
            )

            val data = Project("kotlinx.serialization")
            println(data)
            println(format.encodeToString(data))

        }

    }

    @Test
    fun colorTesting() {

        val blue = 0x0000ff
        val red = 0xff0000
        //#ffd600
        println("Blue".color(blue))
        println("Red".color(red))
        println("Purple".color(blue + red))
        println("Purple".color(0xff00ff))
        println("Purple".color(blendARGB(blue, red, 0.5f)))
        println("Purple".color(mergeColors(blue, red)))
        /*repeat(10) {
            val c = Random.nextColor()
            println(c)
            println("Random".color(c))
        }*/
        val y = 0xffd600
        println("Yellow".color(y))
        val c = y.toRGB()
        println(c)
        println(c.toInt())
        println("Yellow".color(c.toInt()))
        println(y.toARGB().toInt())
        println("Yellow".color(y.toRGB()))

        val now = 37850000L
        println(now.stringForTime())
        val now1 = 10.hours + 30.minutes + 50.seconds
        println(now1.inMilliseconds.stringForTime())
        println(now1.stringForTime())
        println(now1.inMilliseconds.toLong())

        println(now1[HelpfulUnit.MINUTES])

    }

    private fun String.color(color: Colors) = color.toRGB().let { c -> AnsiColor.colorText(this, c.r, c.g, c.b) }

    private fun blendARGB(color1: Int, color2: Int, ratio: Float): Int {
        val inverseRatio = 1 - ratio
        val c = color1.toARGB()
        val c2 = color2.toARGB()
        val a = c.a * inverseRatio + c2.a * ratio
        val r = c.r * inverseRatio + c2.r * ratio
        val g = c.g * inverseRatio + c2.g * ratio
        val b = c.b * inverseRatio + c2.b * ratio
        return Colors.ARGB(a.toInt(), r.toInt(), g.toInt(), b.toInt()).toInt()
        //Colors.ARGB(a, r, g, b).toInt()
        //Color.argb(a.toInt(), r.toInt(), g.toInt(), b.toInt())
    }

    private fun mergeColors(backgroundColor: Int, foregroundColor: Int): Int {
        val ALPHA_CHANNEL: Byte = 24
        val RED_CHANNEL: Byte = 16
        val GREEN_CHANNEL: Byte = 8
        val BLUE_CHANNEL: Byte = 0
        val ap1 = (backgroundColor shr ALPHA_CHANNEL.toInt() and 0xff).toDouble() / 255.0
        val ap2 = (foregroundColor shr ALPHA_CHANNEL.toInt() and 0xff).toDouble() / 255.0
        val ap = ap2 + ap1 * (1 - ap2)
        val amount1 = ap1 * (1 - ap2) / ap
        val amount2 = amount1 / ap
        val a = (ap * 255.0).toInt() and 0xff
        val r = ((backgroundColor shr RED_CHANNEL.toInt() and 0xff).toFloat() * amount1 +
                (foregroundColor shr RED_CHANNEL.toInt() and 0xff).toFloat() * amount2).toInt() and 0xff
        val g = ((backgroundColor shr GREEN_CHANNEL.toInt() and 0xff).toFloat() * amount1 +
                (foregroundColor shr GREEN_CHANNEL.toInt() and 0xff).toFloat() * amount2).toInt() and 0xff
        val b = ((backgroundColor and 0xff).toFloat() * amount1 +
                (foregroundColor and 0xff).toFloat() * amount2).toInt() and 0xff
        return a shl ALPHA_CHANNEL.toInt() or (r shl RED_CHANNEL.toInt()) or (g shl GREEN_CHANNEL.toInt()) or (b shl BLUE_CHANNEL.toInt())
    }

    @Test
    fun wordCountTest() {
        println("olly olly in come free".wordCount())
    }

    private fun String.wordCount() = split(" ").groupBy { it }.map { it.key to it.value.size }.toMap()

    @Test
    fun scrabble() {
        println("cabbage".scrabbleScore())
        println("cabbage".scrabbleScore2())
        println("cabbage".scrabbleScore3())
    }

    private fun String.scrabbleScore(): Int {
        val map = mapOf(
            1 to listOf("A", "E", "I", "O", "U", "L", "N", "R", "S", "T"),
            2 to listOf("D", "G"),
            3 to listOf("B", "C", "M", "P"),
            4 to listOf("F", "H", "V", "W", "Y"),
            5 to listOf("K"),
            8 to listOf("J", "X"),
            10 to listOf("Q", "Z")
        )
        return fold(0) { acc, n -> acc + map.entries.find { n.toString().toUpperCase() in it.value }!!.key }
    }

    private fun String.scrabbleScore2(): Int {
        val map = mapOf(
            1 to "AEIOULNRST",
            2 to "DG",
            3 to "BCMP",
            4 to "FHVWY",
            5 to "K",
            8 to "JX",
            10 to "QZ"
        )
        return fold(0) { acc, n -> acc + map.entries.find { n.toUpperCase() in it.value }!!.key }
    }

    private fun String.scrabbleScore3(): Int {
        val map = mapOf(
            1 to "AEIOULNRST",
            2 to "DG",
            3 to "BCMP",
            4 to "FHVWY",
            5 to "K",
            8 to "JX",
            10 to "QZ"
        )

        return sumBy { n -> map.entries.find { n.toUpperCase() in it.value }!!.key }
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

    interface Person {
        val name: String
        val age: Int
        val canBuyAlcohol: Boolean

        fun helloText(): String

        fun cheerText(person: Person): String
    }

    // * They both should implement Person
    // * They both can buy alcohol only if over 21
    // * Businessman says hello by “Good morning”, Student by “Hi”.
    // * Businessman cheers by “Hello, my name is {his name}, nice to see you {cheered person name}”, Student by “Hey {cheered person name}, I am {his name}”.

    class Businessman(override val name: String, override val age: Int) : Person {
        override val canBuyAlcohol: Boolean = age >= 21
        override fun helloText(): String = "Good morning"
        override fun cheerText(person: Person): String = "Hello, my name is $name, nice to see you ${person.name}"
    }

    class Student(override val name: String, override val age: Int) : Person {
        override val canBuyAlcohol: Boolean = age >= 21
        override fun helloText(): String = "Hi"
        override fun cheerText(person: Person): String = "Hey ${person.name}, I am $name"
    }

    @Test
    fun exampleTestThing() {
        val businessman: Person = Businessman("Johnny", 25)
        val student: Person = Student("Jordan", 20)

        println(businessman.helloText())
        println(student.helloText())

        println(businessman.cheerText(student))
        println(student.cheerText(businessman))

        fun sayIfCanBuyAlcohol(person: Person) {
            val modal = if (person.canBuyAlcohol) "can" else "can't"
            println("${person.name} $modal buy alcohol")
        }

        sayIfCanBuyAlcohol(businessman)
        sayIfCanBuyAlcohol(student)
    }

    data class Human(val name: String, val age: Int)

    data class People(val human: Human)

    @Test
    fun randomDataTest() {
        val a = randomData<People>(People::class.java)
        println(a)
    }

    private fun <T> randomData(clazz: Class<*>): Any {
        val constructor = clazz.constructors.random()
        val params = constructor.parameterCount
        val paramTypes = constructor.parameterTypes
        println(paramTypes.map { it.name })
        val check = params == 0 || params == 1 && paramTypes.getOrNull(0) is Number || paramTypes.getOrNull(0) == String::class.java
        println(check)
        return if (check) {
            when (paramTypes[0]) {
                is Number -> Random.nextInt(0, 100)
                else -> Random.nextString(50)
            }
        } else constructor.newInstance(paramTypes.map { randomData<Any>(it) })
    }

}

class FixedSizeList<T>(maxSize: Int = 1) : FixedList<T>(maxSize) {
    override fun add(element: T): Boolean = super.add(element).also { singleSizeCheck() }
    override fun add(index: Int, element: T) = super.add(index, element).also { singleSizeCheck() }
    override fun addAll(elements: Collection<T>): Boolean = super.addAll(elements).also { multipleSizeCheck() }
    override fun addAll(index: Int, elements: Collection<T>): Boolean = super.addAll(index, elements).also { multipleSizeCheck() }
}

class FixedDeck<T>(cards: Iterable<T> = emptyList()) : AbstractDeck<T>(cards) {
    override val deckOfCards: FixedList<T> = cards.toFixedList(10)
    override fun cardAdded(vararg card: T) {}
    override fun cardDrawn(card: T, size: Int) {}
    override fun deckShuffled() {}
}