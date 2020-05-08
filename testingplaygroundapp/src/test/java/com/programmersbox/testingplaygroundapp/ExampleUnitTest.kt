package com.programmersbox.testingplaygroundapp

import com.programmersbox.funutils.cards.Card
import com.programmersbox.funutils.cards.Deck
import com.programmersbox.funutils.cards.Suit
import com.programmersbox.testingplayground.color
import com.programmersbox.testingplaygroundapp.cardgames.asciiCards
import com.programmersbox.testingplaygroundapp.cardgames.poker.Hand
import com.programmersbox.testingplaygroundapp.cardgames.poker.PokerHand
import org.junit.Test
import kotlin.system.measureTimeMillis

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    @Test
    fun forAll() {
        5 keepAway 6 with { println("First: ${it.first} | Second: ${it.second}") }

        val sparta = "Leonidas"
        //Haha
        this `is` sparta
    }

    class KeepAway(val first: Int, val second: Int)

    private infix fun KeepAway.with(block: (KeepAway) -> Unit) = block(this)
    private infix fun Int.keepAway(n: Int) = KeepAway(this, n)

    private infix fun `is`(s: String) = println("THIS IS $s")

    @Test
    fun other123() {
        val builder = measureTimeMillis {
            val sb = StringBuilder("hello")
            for (i in 0..9999) {
                sb.append(" world")
            }
            val string = sb.toString()
            println(string.length)
        }

        val noBuilder = measureTimeMillis {
            var sb = "hello"
            for (i in 0..9999) {
                sb += " world"
            }
            val string = sb
            println(string.length)
        }

        println("builder: $builder")
        println("noBuilder: $noBuilder")
    }

    @Test
    fun other() {
        val d = Deck.defaultDeck()
        println(d)
    }

    @Suppress("UNUSED_VARIABLE")
    @Test
    fun addition_isCorrect() {
        val d = Deck.defaultDeck()
        d.trueRandomShuffle()
        println(d)
        while (d.size >= 5) d.draw(5).printCards()
        println("-".repeat(50))
        val royalFlush = listOf(1, 10, 11, 12, 13).map { Card(it, Suit.SPADES) }.printCards()
        val straightFlush = listOf(9, 10, 11, 12, 13).map { Card(it, Suit.SPADES) }.printCards()
        val fourKind = listOf(13, 13, 13, 13, 6).map { Card(it, Suit.values().random()) }.printCards()
        val fullHouse = listOf(2, 2, 2, 4, 4).map { Card(it, Suit.values().random()) }.printCards()
        val flush = listOf(1, 10, 11, 12, 9).map { Card(it, Suit.SPADES) }.printCards()
        val straight = listOf(9, 10, 11, 12, 13).map { Card(it, Suit.values().random()) }.printCards()
        val threeKind = listOf(2, 2, 2, 4, 5).map { Card(it, Suit.values().random()) }.printCards()
        val twoPair = listOf(2, 2, 4, 4, 5).map { Card(it, Suit.values().random()) }.printCards()
        val pair = listOf(1, 1, 2, 3, 4).map { Card(it, Suit.values().random()) }.printCards()
        val nothing = listOf(1, 3, 5, 7, 9).map { Card(it, Suit.values().random()) }.printCards()
    }

    private fun List<Card>.printCards() = asciiCards(colorRed = 0xD01426, colorBlack = 0x039b3f0) {
        PokerHand.getWinningHand(it).let { hand -> " = ${it.joinToString { it.toSymbolString() }} = ${hand.stringName}".color(hand.getColorLevel) }
    }.let(::println)

    private val Hand.getColorLevel
        get() = when (this) {
            Hand.ROYAL_FLUSH -> 0xd4af37
            Hand.STRAIGHT_FLUSH -> 0xc0c0c0
            Hand.FOUR_KIND -> 0xb08d57
            Hand.FULL_HOUSE -> 0xFFA500
            Hand.FLUSH -> 0xff0000
            Hand.STRAIGHT -> 0xFFC0CB
            Hand.THREE_KIND -> 0xFF00FF
            Hand.TWO_PAIR -> 0x00ff00
            Hand.PAIR -> 0xffff00
            Hand.NOTHING -> 0xffffff
        }

}

/*
fun main() {
    val scan = Scanner(System.`in`)
    val d = Deck.defaultDeck()
    d.trueRandomShuffle()
    println(d.draw(5).asciiCards(colorRed = 0xD01426, colorBlack = 0x039b3f0))
    while (d.size > 5) println(d.draw(5).asciiCards(colorRed = 0xD01426, colorBlack = 0x039b3f0))
    println("Enter Info")
    val s = scan.nextLine()
    println("Here is your info: $s")
}
*/
