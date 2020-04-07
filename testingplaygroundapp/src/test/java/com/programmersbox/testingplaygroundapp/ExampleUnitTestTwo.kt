package com.programmersbox.testingplaygroundapp

import com.programmersbox.funutils.cards.AbstractDeck
import com.programmersbox.helpfulutils.sizedListOf
import com.programmersbox.testingplayground.color
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
            cardPlayed { played, player ->
                if (played) println("$count. ${player.name} played ${unoGame.topCard?.type?.color(unoGame.topCard?.color?.getColor ?: 0xffffff)}")
                    .also { println(unoGame.players.joinToString("\n") { "${it.name} = ${it.hand.joinToString { it.type.color(it.color.getColor) }}" }) }
                    .also { unoGame.topCard?.let { println("Top card: ${it.type}".color(it.color.getColor)) } }
                    .also { count++ }
            }
        }

        println(unoGame.players.joinToString("\n") { "${it.name} = ${it.hand.joinToString { it.type.color(it.color.getColor) }}" })

        while (unoGame.players.all { it.hand.isNotEmpty() })
            unoGame.currentPlayer.hand.find(unoGame::isPlayable)?.let(unoGame::playCard) ?: unoGame.noCardDraw()

        println("${unoGame.players.find { it.hand.isEmpty() }?.name} won".color(0x00ff00))
    }

}

enum class UnoColor {
    RED, BLUE, GREEN, YELLOW, BLACK;

    val getColor
        get() = when (this) {
            RED -> 0xff0000
            BLUE -> 0x4499ff
            GREEN -> 0x00ff00
            YELLOW -> 0xffff00
            BLACK -> 0x040404
        }

    companion object {
        val playableColors = arrayOf(BLUE, GREEN, RED, YELLOW)
    }
}

data class UnoCard(val value: Int, val color: UnoColor) {
    val type = when (value) {
        10 -> "Skip"
        11 -> "+2"
        12 -> "Reverse"
        13 -> "Wild"
        14 -> "Wild +4"
        else -> "$value"
    } + " $color"

    val action: (UnoGame) -> Unit
        get() = when (value) {
            10 -> UnoGame::nextTurn
            11 -> { game -> game.draw(2) }
            12 -> UnoGame::reverse
            13 -> UnoGame::wildCard
            14 -> UnoGame::wildDrawFour
            else -> { _ -> }
        }
}

open class UnoPlayer(val name: String) {
    val hand = mutableListOf<UnoCard>()
    fun playCard(index: Int) = hand.removeAt(index)
    fun playCard(card: UnoCard) = hand.remove(card)
    operator fun get(index: Int) = hand[index]
}

class UnoGame(vararg player: UnoPlayer) {

    private var playerPlay = 0

    private var listener: UnoInterface? = null

    val players = player.toList()

    private val playedCards = UnoDeck()

    private val deck = UnoDeck().apply {
        val colors = arrayOf(UnoColor.RED, UnoColor.BLUE, UnoColor.GREEN, UnoColor.YELLOW)
        repeat(2) {
            for (j in colors) {
                for (i in 1..12) addCard(UnoCard(i, j))
            }
        }
        for (j in colors) addCard(UnoCard(0, j))
        for (i in 0..3) addCard(UnoCard(13, UnoColor.BLACK))
        for (i in 0..3) addCard(UnoCard(14, UnoColor.BLACK))
        trueRandomShuffle()
    }

    private var order = true

    init {
        players.forEach { it.hand += deck.draw(7) }
        sortHands()
    }

    fun setListener(block: UnoInterface.UnoListener.() -> Unit) = run { listener = UnoInterface.build(block) }
        .also { listener?.orderChanged((if (order) players else players.reversed()).map(UnoPlayer::name).toTypedArray()) }

    val currentPlayer get() = players[playerPlay]

    val topCard get() = playedCards.deck.lastOrNull()

    private fun sortHands() = players.forEach { it.hand.sortWith(compareBy(UnoCard::color).thenBy(UnoCard::value)) }

    fun nextTurn() {
        playerPlay += if (order) 1 else -1
        if (playerPlay > players.size - 1) playerPlay = 0
        else if (playerPlay < 0) playerPlay = players.size - 1
        addCardsToDeck()
        sortHands()
    }

    private fun addCardsToDeck() {
        if (deck.size < 5) {
            val last = playedCards.draw()
            deck.addUnoDeck(playedCards)
            listener?.addCardsToDeck(*playedCards.deck.toTypedArray())
            playedCards.removeAllCards()
            playedCards.addUnoCards(last)
        }
    }

    fun noCardDraw() {
        currentPlayer.hand += deck.draw(1)
        nextTurn()
    }

    fun draw(amount: Int = 2) {
        nextTurn()
        currentPlayer.hand += deck.draw(amount)
    }

    fun reverse() {
        order = !order
        listener?.orderChanged((if (order) players else players.reversed()).map(UnoPlayer::name).toTypedArray())
    }

    fun wildCard() {
        val card = playedCards.lastCard
        playedCards.remove(card)
        val newCard = UnoCard(card.value, listener?.wildCard(currentPlayer) ?: UnoColor.playableColors.random())
        playedCards.addCard(newCard)
    }

    fun wildDrawFour() {
        wildCard()
        draw(4)
    }

    fun isPlayable(card: UnoCard) = playedCards.isEmpty ||
            playedCards.lastCard.color == card.color || playedCards.lastCard.value == card.value ||
            card.value == 13 || card.value == 14

    fun playCard(index: Int) {
        val card = currentPlayer[index]
        if (isPlayable(card)) {
            currentPlayer.playCard(index)
            playedCards.addCard(card)
            card.action(this)
            listener?.cardPlayed(true, currentPlayer)
            nextTurn()
        } else listener?.cardPlayed(false, currentPlayer)
    }

    fun playCard(card: UnoCard) {
        if (playedCards.isEmpty || isPlayable(card)) {
            currentPlayer.playCard(card)
            playedCards.addCard(card)
            card.action(this)
            listener?.cardPlayed(true, currentPlayer)
            nextTurn()
        } else listener?.cardPlayed(false, currentPlayer)
    }
}

@DslMarker
annotation class UnoMarker

interface UnoInterface {
    fun wildCard(player: UnoPlayer): UnoColor
    fun cardPlayed(played: Boolean, player: UnoPlayer)
    fun orderChanged(order: Array<String>)
    fun addCardsToDeck(vararg card: UnoCard)

    companion object {
        fun build(block: UnoListener.() -> Unit) = UnoListener().apply(block).build()
    }

    class UnoListener {

        private var wild: (player: UnoPlayer) -> UnoColor = { UnoColor.BLACK }

        @UnoMarker
        fun wild(block: (player: UnoPlayer) -> UnoColor) = run { wild = block }

        private var playedCard: (Boolean, UnoPlayer) -> Unit = { _, _ -> }

        @UnoMarker
        fun cardPlayed(block: (played: Boolean, player: UnoPlayer) -> Unit) = run { playedCard = block }

        private var changeOrder: (Array<String>) -> Unit = {}

        @UnoMarker
        fun orderChanged(block: (Array<String>) -> Unit) = run { changeOrder = block }

        private var addToDeck: (Array<out UnoCard>) -> Unit = {}

        @UnoMarker
        fun addCardsToDeck(block: (Array<out UnoCard>) -> Unit) = run { addToDeck = block }

        fun build() = object : UnoInterface {
            override fun wildCard(player: UnoPlayer): UnoColor = wild(player)
            override fun cardPlayed(played: Boolean, player: UnoPlayer) = playedCard(played, player)
            override fun orderChanged(order: Array<String>) = changeOrder(order)
            override fun addCardsToDeck(vararg card: UnoCard) = addToDeck(card)
        }
    }
}

class UnoDeck(cards: Iterable<UnoCard> = emptyList()) : AbstractDeck<UnoCard>(cards) {
    override fun cardAdded(vararg card: UnoCard) {}
    override fun cardDrawn(card: UnoCard, size: Int) {}
    override fun deckShuffled() {}
    fun addUnoDeck(deck: UnoDeck) = deckOfCards.addAll(deck.deck)
    fun addUnoCards(vararg card: UnoCard) = deckOfCards.addAll(card)
}
