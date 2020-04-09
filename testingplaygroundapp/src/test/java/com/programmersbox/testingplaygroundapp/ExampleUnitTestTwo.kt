package com.programmersbox.testingplaygroundapp

import com.programmersbox.helpfulutils.sizedListOf
import com.programmersbox.testingplayground.color
import com.programmersbox.testingplaygroundapp.cardgames.uno.UnoColor
import com.programmersbox.testingplaygroundapp.cardgames.uno.UnoGame
import com.programmersbox.testingplaygroundapp.cardgames.uno.UnoPlayer
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.random.Random

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

}
