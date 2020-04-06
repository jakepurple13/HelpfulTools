package com.programmersbox.testingplaygroundapp

import com.programmersbox.funutils.cards.Deck
import com.programmersbox.testingplaygroundapp.cardgames.asciiCards
import org.junit.Test
import java.util.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    @Test
    fun addition_isCorrect() {
        val d = Deck.defaultDeck()
        d.trueRandomShuffle()
        println(d.draw(5).asciiCards(colorRed = 0xD01426, colorBlack = 0x039b3f0))
        while (d.size > 5) println(d.draw(5).asciiCards(colorRed = 0xD01426, colorBlack = 0x039b3f0) { it.joinToString { it.toSymbolString() } })
    }

}

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