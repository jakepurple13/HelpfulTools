package com.programmersbox.testingplayground

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.launch

fun <T> Iterable<T>.toFlowDeck() = FlowDeck(this)
fun <T> Array<T>.toFlowDeck() = FlowDeck(*this)
class FlowDeck<T> : AbstractDeck<T> {

    constructor(cards: Iterable<T> = emptyList()) : super(cards)
    constructor(vararg cards: T) : super(cards.toList())

    private val onAddChannel = BroadcastChannel<List<T>>(1)
    private val onDrawChannel = BroadcastChannel<DrawInfo>(1)
    private val onShuffleChannel = BroadcastChannel<Unit>(1)

    var channelMove = FlowDeckMoveType.SEND

    enum class FlowDeckMoveType { SEND, OFFER }

    inner class DrawInfo(val card: T, val size: Int)

    private fun <R> sendChannel(channel: BroadcastChannel<R>, item: R) = GlobalScope.launch {
        when (channelMove) {
            FlowDeckMoveType.SEND -> channel.send(item)
            FlowDeckMoveType.OFFER -> channel.offer(item)
        }
    }.let { Unit }

    override fun cardAdded(vararg card: T) = sendChannel(onAddChannel, card.toList())
    override fun cardDrawn(card: T, size: Int) = sendChannel(onDrawChannel, DrawInfo(card, size))
    override fun deckShuffled() = sendChannel(onShuffleChannel, Unit)

    fun onAddCollect() = onAddChannel.asFlow()
    fun onDrawCollect() = onDrawChannel.asFlow()
    fun onShuffleCollect() = onShuffleChannel.asFlow()

    companion object {
        /**
         * A default card_games.Deck of Playing Cards
         */
        fun defaultDeck() = FlowDeck(*Suit.values().map { suit -> (1..13).map { value -> Card(value, suit) } }.flatten().toTypedArray())

        /**
         * Create a deck by adding a card to it!
         */
        operator fun <T> plus(card: T) = FlowDeck(card)
    }
}