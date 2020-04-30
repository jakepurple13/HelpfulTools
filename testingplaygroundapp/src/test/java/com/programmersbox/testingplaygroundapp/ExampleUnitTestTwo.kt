package com.programmersbox.testingplaygroundapp

import com.programmersbox.helpfulutils.sizedListOf
import com.programmersbox.testingplayground.color
import com.programmersbox.testingplaygroundapp.cardgames.uno.UnoColor
import com.programmersbox.testingplaygroundapp.cardgames.uno.UnoGame
import com.programmersbox.testingplaygroundapp.cardgames.uno.UnoPlayer
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.random.Random
import kotlin.reflect.KProperty

class ExampleUnitTestTwo {

    @ExperimentalStdlibApi
    @Test
    fun unoTest() = runBlocking {
        var count = 1
        val players = sizedListOf(Random.nextInt(3, 10)) { UnoPlayer(getRandomName()) }
        val unoGame = UnoGame(*players.toTypedArray())
        unoGame.setListener {
            wild { it.hand.filter { it.color != UnoColor.BLACK }.randomOrNull()?.color ?: UnoColor.playableColors.random() }
            orderChanged { println("Order -> ${it.joinToString { it }}") }
            addCardsToDeck { println("Cards being added to deck".color(0x040404)) }
            cardPlayed { played, player, card ->
                if (played) println("$count. ${player.name} played ${card.type.color(card.color.getColor)}")
                    .also { unoGame.topCard?.let { println("Top card: ${it.type}".color(it.color.getColor)) } }
                    .also { println(unoGame.players.joinToString("\n") { "${it.name} = ${it.hand.joinToString { it.type.color(it.color.getColor) }}" }) }
                    .also { count++ }
            }
        }

        println(unoGame.players.joinToString("\n") { "${it.name} = ${it.hand.joinToString { it.type.color(it.color.getColor) }}" })

        while (unoGame.players.all { it.hand.isNotEmpty() })
            unoGame.currentPlayer.hand.find(unoGame::isPlayable)?.let(unoGame::playCard) ?: unoGame.noCardDraw()

        println("${unoGame.players.find { it.hand.isEmpty() }?.name} won".color(0x00ff00))
    }

    @Test
    fun other() {
        var b: String by Delegate("Stuff")

        println(b)
        b = "Hello"
        println(b)
        b = "World"
        println(b)

    }

    class Delegate<T>(defaultValue: T) {

        private var s: T = defaultValue

        operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
            return s//"$thisRef, thank you for delegating '${property.name}' to me!"
        }

        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
            println("$value has been assigned to '${property.name}' in $thisRef.")
            s = value
        }
    }

}
