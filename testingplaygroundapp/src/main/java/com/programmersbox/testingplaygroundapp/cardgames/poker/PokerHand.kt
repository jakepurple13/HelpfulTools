package com.programmersbox.testingplaygroundapp.cardgames.poker

import com.programmersbox.funutils.cards.Card

enum class Hand(val stringName: String, val defaultWinning: Int) {
    ROYAL_FLUSH("Royal Flush", 250),
    STRAIGHT_FLUSH("Straight Flush", 50),
    FOUR_KIND("Four of a Kind", 25),
    FULL_HOUSE("Full House", 9),
    FLUSH("Flush", 6),
    STRAIGHT("Straight", 4),
    THREE_KIND("Three of a Kind", 3),
    TWO_PAIR("Two Pair", 2),
    PAIR("Pair", 1),
    NOTHING("Nothing", 0)
}

object PokerHand {

    fun getWinningHand(cards: List<Card>): Hand = when {
        royalFlush(cards) -> Hand.ROYAL_FLUSH
        straightFlush(cards) -> Hand.STRAIGHT_FLUSH
        fourKind(cards) -> Hand.FOUR_KIND
        fullHouse(cards) -> Hand.FULL_HOUSE
        flush(cards) -> Hand.FLUSH
        straight(cards) -> Hand.STRAIGHT
        threeKind(cards) -> Hand.THREE_KIND
        twoPair(cards) -> Hand.TWO_PAIR
        pair(cards) -> Hand.PAIR
        else -> Hand.NOTHING
    }

    private fun List<Card>.groupByValue() = groupBy(Card::value)

    private fun royalFlush(cards: List<Card>): Boolean = cards.sortedBy(Card::value)
        .let { it[1].value == 10 && it[2].value == 11 && it[3].value == 12 && it[4].value == 13 && it[0].value == 1 && flush(it) }

    private fun straightFlush(cards: List<Card>) = straight(cards) && flush(cards)
    private fun fourKind(cards: List<Card>) = cards.groupByValue().any { it.value.size == 4 }
    private fun fullHouse(cards: List<Card>) = threeKind(cards) && pair(cards)
    private fun flush(cards: List<Card>) = cards.all { it.suit == cards[0].suit }
    private fun straight(cards: List<Card>): Boolean {
        val h = cards.sortedBy(Card::value)
        for (i in 0 until h.size - 1) {
            var value = h[i].value
            if (value == 1) if (h[i + 1].value == 10) value = 9 //ace check
            if (value + 1 != h[i + 1].value) return false
        }
        return true
    }

    private fun threeKind(cards: List<Card>) = cards.groupByValue().any { it.value.size == 3 }
    private fun twoPair(cards: List<Card>) = cards.groupByValue().entries.count { it.value.size == 2 } == 2
    private fun pair(cards: List<Card>) = cards.groupByValue().any { it.value.size == 2 }

}