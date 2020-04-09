package com.programmersbox.testingplaygroundapp.cardgames.uno

import androidx.core.graphics.toColorInt
import com.programmersbox.funutils.cards.AbstractDeck

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

    val getHexColor
        get() = when (this) {
            RED -> "#ff0000"
            BLUE -> "#4499ff"
            GREEN -> "#00ff00"
            YELLOW -> "#ffff00"
            BLACK -> "#040404"
        }.toColorInt()

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

data class UnoPlayer(val name: String) {
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
        playedCards.addUnoCards(deck.draw())
    }

    fun setListener(block: UnoInterface.UnoListener.() -> Unit) = run { listener = UnoInterface.build(block) }
        .also { listener?.orderChanged((if (order) players else players.reversed()).map(UnoPlayer::name).toTypedArray()) }

    val previousPlayer get() = players[nextPlayerIndex(!order)]
    val currentPlayer get() = players[playerPlay]
    val nextPlayer get() = players[nextPlayerIndex(order)]

    val topCard get() = playedCards.deck.lastOrNull()

    private val nextPlayerIndex: (Boolean) -> Int = {
        var p = playerPlay
        p += if (it) 1 else -1
        if (p > players.size - 1) p = 0
        else if (p < 0) p = players.size - 1
        p
    }

    private fun sortHands() = players.forEach { it.hand.sortWith(compareBy(UnoCard::color).thenBy(UnoCard::value)) }

    fun nextTurn() {
        playerPlay += if (order) 1 else -1
        if (playerPlay > players.size - 1) playerPlay = 0
        else if (playerPlay < 0) playerPlay = players.size - 1
        addCardsToDeck()
        sortHands()
        println("Previous: $previousPlayer")
        println("Current: $currentPlayer")
        println("Next: $nextPlayer")
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
        listener?.cardDrawn()
        nextTurn()
    }

    fun draw(amount: Int = 2) {
        nextPlayer.hand += deck.draw(amount)
        listener?.cardDrawn()
        nextTurn()
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

    fun wildCardTwo(color: UnoColor) {
        val card = playedCards.lastCard
        playedCards.remove(card)
        val newCard = UnoCard(card.value, color)
        playedCards.addCard(newCard)
        listener?.cardPlayed(true, currentPlayer, newCard)
    }

    fun wildDrawFour() {
        wildCard()
        draw(4)
    }

    fun isPlayable(card: UnoCard) = playedCards.isEmpty ||
            playedCards.lastCard.color == card.color || playedCards.lastCard.value == card.value ||
            card.value == 13 || card.value == 14

    fun playCard(card: UnoCard) {
        if (playedCards.isEmpty || isPlayable(card)) {
            currentPlayer.playCard(card)
            playedCards.addCard(card)
            card.action(this)
            listener?.cardPlayed(true, currentPlayer, card)
            nextTurn()
        } else listener?.cardPlayed(false, currentPlayer, card)
    }
}

@DslMarker
annotation class UnoMarker

interface UnoInterface {
    fun wildCard(player: UnoPlayer): UnoColor
    fun cardPlayed(played: Boolean, player: UnoPlayer, card: UnoCard)
    fun orderChanged(order: Array<String>)
    fun addCardsToDeck(vararg card: UnoCard)
    fun cardDrawn()

    companion object {
        fun build(block: UnoListener.() -> Unit) = UnoListener().apply(block).build()
    }

    class UnoListener {

        private var wild: (player: UnoPlayer) -> UnoColor = { UnoColor.BLACK }

        @UnoMarker
        fun wild(block: (player: UnoPlayer) -> UnoColor) = run { wild = block }

        private var playedCard: (Boolean, UnoPlayer, UnoCard) -> Unit = { _, _, _ -> }

        @UnoMarker
        fun cardPlayed(block: (played: Boolean, player: UnoPlayer, card: UnoCard) -> Unit) = run { playedCard = block }

        private var changeOrder: (Array<String>) -> Unit = {}

        @UnoMarker
        fun orderChanged(block: (Array<String>) -> Unit) = run { changeOrder = block }

        private var addToDeck: (Array<out UnoCard>) -> Unit = {}

        @UnoMarker
        fun addCardsToDeck(block: (Array<out UnoCard>) -> Unit) = run { addToDeck = block }

        private var drawCard: () -> Unit = {}

        @UnoMarker
        fun cardDrawn(block: () -> Unit) = run { drawCard = block }

        fun build() = object : UnoInterface {
            override fun wildCard(player: UnoPlayer): UnoColor = wild(player)
            override fun cardPlayed(played: Boolean, player: UnoPlayer, card: UnoCard) = playedCard(played, player, card)
            override fun orderChanged(order: Array<String>) = changeOrder(order)
            override fun addCardsToDeck(vararg card: UnoCard) = addToDeck(card)
            override fun cardDrawn() = drawCard()
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