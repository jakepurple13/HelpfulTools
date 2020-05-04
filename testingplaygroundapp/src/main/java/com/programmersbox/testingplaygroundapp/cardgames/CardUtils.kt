package com.programmersbox.testingplaygroundapp.cardgames

import android.content.Context
import com.programmersbox.funutils.cards.Card
import com.programmersbox.funutils.cards.CardColor
import com.programmersbox.funutils.cards.Suit
import com.programmersbox.loggingutils.FrameType
import com.programmersbox.loggingutils.frame
import com.programmersbox.testingplayground.color

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

fun List<Card>.asciiCards(padding: String = "", colorRed: Int = 0xff0000, colorBlack: Int = 0xffffff, equalText: (List<Card>) -> String): String {
    val lines = asciiCards(padding, colorRed, colorBlack).lines().toMutableList()
    lines[lines.size / 2 - 1] += equalText(this)
    return lines.joinToString("\n")
}

fun List<Card>.asciiCards(padding: String = "", colorRed: Int = 0xff0000, colorBlack: Int = 0xffffff): String {
    val m = map { it.color to it.asciiCard().lines() }
    val size = m.random().second.size - 1
    val s = StringBuilder()
    fun CardColor.asciiColor() = when (this) {
        CardColor.RED -> colorRed
        CardColor.BLACK -> colorBlack
    }
    for (i in 0..size) s.append(m.joinToString(padding) { it.second[i].color(it.first.asciiColor()) }).append("\n")
    return s.toString()
}

fun Card.asciiCard(): String {
    val spaceLength = 10
    val symbol = toSymbolString()
    val spaces = fun(num: Int) = " ".repeat(num)
    return listOf(
        " $symbol${spaces(spaceLength - symbol.length)}",
        "",
        "",
        "${spaces(spaceLength / 2)}${suit.unicodeSymbol}${spaces(spaceLength / 2)}",
        "",
        "",
        "${spaces(spaceLength - symbol.length)}$symbol "
    ).frame(FrameType.OVAL)
}
