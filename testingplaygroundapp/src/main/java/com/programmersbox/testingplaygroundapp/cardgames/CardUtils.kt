package com.programmersbox.testingplaygroundapp.cardgames

import android.content.Context
import com.programmersbox.funutils.cards.Card
import com.programmersbox.funutils.cards.Suit

val Card.valueTen: Int get() = if (value > 10) 10 else value

fun Card.getImage(context: Context): Int = context.resources.getIdentifier(getCardName(), "drawable", context.packageName)

private fun Card.getCardName() = if (cardName(value) == "clear" || cardName(value) == "b1fv")
    cardName(value)
else {
    cardName(value) + when (suit) {
        Suit.CLUBS -> 1
        Suit.SPADES -> 2
        Suit.HEARTS -> 3
        Suit.DIAMONDS -> 4
    }
}

private fun cardName(num: Int): String = when (num) {
    1 -> "ace"
    2 -> "two"
    3 -> "three"
    4 -> "four"
    5 -> "five"
    6 -> "six"
    7 -> "seven"
    8 -> "eight"
    9 -> "nine"
    10 -> "ten"
    11 -> "jack"
    12 -> "queen"
    13 -> "king"
    15 -> "clear"
    else -> "b1fv"
}