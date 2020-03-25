package com.programmersbox.testingplayground

import kotlin.properties.Delegates

@DslMarker
annotation class CardMarker

enum class CardColor { BLACK, RED }
data class Card(val value: Int, val suit: Suit) {
    val valueTen: Int get() = if (value > 10) 10 else value
    val color: CardColor get() = suit.color
    val symbol: String
        get() = when (value) {
            13 -> "K"
            12 -> "Q"
            11 -> "J"
            1 -> "A"
            else -> "$value"
        }

    fun toSymbolString() = "$symbol${suit.unicodeSymbol}"

    @DeckMarker
    class CardBuilder {
        var value by Delegates.notNull<Int>()
        var suit by Delegates.notNull<Suit>()
        private fun build() = Card(value, suit)

        companion object {
            @CardMarker
            operator fun invoke(block: CardBuilder.() -> Unit) = cardBuilder(block)

            @CardMarker
            fun cardBuilder(block: CardBuilder.() -> Unit) = CardBuilder().apply(block).build()
        }
    }

    companion object {
        val RandomCard: Card get() = Card((1..13).random(), Suit.values().random())
        operator fun get(suit: Suit) = Card((1..13).random(), suit)
        operator fun get(vararg suit: Suit) = suit.map { Card((1..13).random(), it) }
        operator fun get(num: Int) = Card(num, Suit.values().random())
        operator fun get(vararg num: Int) = num.map { Card(it, Suit.values().random()) }
    }
}

enum class Suit(val printableName: String, val symbol: String, val unicodeSymbol: String, val color: CardColor) {
    SPADES("Spades", "S", "♠", CardColor.BLACK),
    CLUBS("Clubs", "C", "♣", CardColor.BLACK),
    DIAMONDS("Diamonds", "D", "♦", CardColor.RED),
    HEARTS("Hearts", "H", "♥", CardColor.RED);
}