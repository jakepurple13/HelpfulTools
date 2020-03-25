package com.programmersbox.testingplayground

import com.programmersbox.rxutils.invoke
import io.reactivex.subjects.PublishSubject

fun <T> Iterable<T>.toRxDeck() = RxDeck(this)
fun <T> Array<T>.toRxDeck() = RxDeck(*this)
class RxDeck<T> : AbstractDeck<T> {

    constructor(cards: Iterable<T> = emptyList()) : super(cards)
    constructor(vararg cards: T) : super(cards.toList())

    private val onAddPublisher = PublishSubject.create<List<T>>()
    private val onDrawPublisher = PublishSubject.create<DrawInfo>()
    private val onShufflePublisher = PublishSubject.create<Unit>()

    inner class DrawInfo(val card: T, val size: Int)

    override fun cardAdded(vararg card: T) = onAddPublisher(card.toList())
    override fun cardDrawn(card: T, size: Int) = onDrawPublisher(DrawInfo(card, size))
    override fun deckShuffled() = onShufflePublisher(Unit)

    fun onAddSubscribe() = onAddPublisher
    fun onDrawSubscribe() = onDrawPublisher
    fun onShuffleSubscribe() = onShufflePublisher

    companion object {
        /**
         * A default card_games.Deck of Playing Cards
         */
        fun defaultDeck() = RxDeck(*Suit.values().map { suit -> (1..13).map { value -> Card(value, suit) } }.flatten().toTypedArray())

        /**
         * Create a deck by adding a card to it!
         */
        operator fun <T> plus(card: T) = RxDeck(card)
    }
}