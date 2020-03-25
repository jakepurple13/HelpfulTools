package com.programmersbox.testingplayground

import kotlin.random.Random

class DeckException(message: String?) : Exception(message)

abstract class AbstractDeck<T>(cards: Iterable<T> = emptyList()) {

    constructor(vararg cards: T) : this(cards.toList())

    protected val deckOfCards: MutableList<T> = cards.toMutableList()

    /**
     * The size of the deck
     */
    val size: Int get() = deckOfCards.size

    /**
     * A non immutable version of the deck
     */
    val deck: List<T> get() = deckOfCards

    /**
     * Checks if the deck is empty
     */
    val isEmpty get() = deckOfCards.isEmpty()

    /**
     * Checks if the deck is not empty
     */
    val isNotEmpty get() = deckOfCards.isNotEmpty()

    /**
     * Gets a random card
     * # Does Not Draw #
     * @throws DeckException if deck is empty
     */
    val randomCard get() = deckOfCards.random()

    /**
     * Gets the first card in the deck
     *  # Does Not Draw #
     * @throws DeckException if deck is empty
     */
    val firstCard get() = deckOfCards.first()

    /**
     * Gets the middle card in the deck
     * # Does Not Draw #
     * @throws DeckException if deck is empty
     */
    val middleCard get() = deckOfCards[size / 2]

    /**
     * Gets the last card in the deck
     * # Does Not Draw #
     * @throws DeckException if deck is empty
     */
    val lastCard get() = deckOfCards.last()

    protected abstract fun cardAdded(vararg card: T)
    protected abstract fun cardDrawn(card: T, size: Int)
    protected abstract fun deckShuffled()

    @Suppress("UNCHECKED_CAST")
    protected fun Iterable<T>.toArray() = Array(this.count()) { i -> this.toList()[i] as Any } as Array<T>
    protected fun MutableList<T>.addCards(vararg card: T) = addAll(card).also { cardAdded(*card) }
    protected fun MutableList<T>.drawCard(index: Int = 0) = tryCatch("Deck is Empty") { removeAt(index).also { cardDrawn(it, size) } }
    protected fun MutableList<T>.removeCards(cards: Iterable<T>) = filter { it in cards }
        .let { filtered -> removeAll(filtered).also { filtered.forEach { c -> cardDrawn(c, size) } } }

    /**
     * Draws a card_games.Card!
     * @throws DeckException if the card_games.Deck is Empty
     */
    @Throws(DeckException::class)
    fun draw() = deckOfCards.drawCard()

    /**
     * Draws multiple Cards!
     * @throws DeckException if the card_games.Deck is Empty
     */
    @Throws(DeckException::class)
    infix fun draw(amount: Int) = tryCatch("Deck is Empty") { mutableListOf<T>().apply { repeat(amount) { this += draw() } }.toList() }

    /**
     * Add [card] to the deck in the [index] location
     */
    fun addCard(index: Int, card: T) = deckOfCards.add(index, card).also { cardAdded(card) }

    /**
     * Add [cards] to the deck in the Int location
     */
    fun addCard(vararg cards: Pair<Int, T>) = cards.forEach { addCard(it.first, it.second) }

    /**
     * Add [card] to the deck
     */
    fun addCard(vararg card: T) = deckOfCards.addCards(*card)

    /**
     * Add [card] to the deck
     */
    infix fun add(card: T) = deckOfCards.addCards(card)

    /**
     * Adds all cards from [otherDeck] to this deck
     */
    infix fun addDeck(otherDeck: AbstractDeck<T>) = deckOfCards.addCards(*otherDeck.deck.toArray())

    /**
     * Add [cards] to the deck
     */
    infix fun addCards(cards: Iterable<T>) = cards.let { deckOfCards.addCards(*cards.toArray()) }

    /**
     * Add [cards] to the deck
     */
    infix fun addCards(cards: Array<T>) = deckOfCards.addCards(*cards)

    /**
     * Gets cards from [cards]
     */
    fun getCards(vararg cards: T) = drawCards { it in cards }

    /**
     * Find cards that match the [predicate]
     */
    infix fun findCards(predicate: (T) -> Boolean) = deckOfCards.filter(predicate)

    /**
     * Find the location of [card]
     */
    infix fun findCardLocation(card: T) = deckOfCards.indexOf(card).let { if (it == -1) null else it }

    /**
     * Draws cards that match [predicate]
     */
    infix fun drawCards(predicate: (T) -> Boolean) = findCards(predicate).also { deckOfCards.removeCards(it) }

    /**
     * Removes [card]
     */
    infix fun remove(card: T) = deckOfCards.remove(card).also { if (it) cardDrawn(card, size) }

    /**
     * Sort thee deck by more than one selector
     */
    fun sortDeck(comparator: Comparator<T>) = deckOfCards.sortWith(comparator)

    /**
     * Sort the deck by one selector
     */
    fun <R> sortDeckBy(selector: (T) -> R?) where R : Comparable<R> = deckOfCards.sortBy(selector)

    /**
     * Removes [card]
     */
    fun remove(vararg card: T) = deckOfCards.filter { it in card }.let { deckOfCards.removeCards(it) }

    /**
     * Shuffles the deck
     */
    fun shuffle(seed: Long? = null) = deckOfCards.shuffle(seed?.let { Random(seed) } ?: Random.Default).also { deckShuffled() }

    /**
     * Truly shuffles the deck by shuffling it 7 times
     */
    fun trueRandomShuffle(seed: Long? = null) = repeat(7) { shuffle(seed) }

    /**
     * Completely clears the deck
     */
    fun clear() = deckOfCards.clear()

    /**
     * Reverses the order of the deck
     */
    fun reverse() = deckOfCards.reverse()

    /**
     * Randomly gets a card
     * @throws DeckException if none of the cards match the [predicate]
     */
    @Throws(DeckException::class)
    fun random(predicate: (T) -> Boolean = { true }) = deck.filter(predicate).tryCatch("Card Not Found") { it.random() }

    /**
     * Randomly draws a card
     * @throws DeckException if none of the cards match the [predicate]
     */
    @Throws(DeckException::class)
    fun randomDraw(predicate: (T) -> Boolean = { true }) = deck.filter(predicate).tryCatch("Card Not Found") { it.random().also { c -> remove(c) } }

    override fun toString(): String = deck.toString()

    /**
     * Adds the cards from [deck] to this deck
     */
    operator fun invoke(deck: AbstractDeck<T>) = addDeck(deck)

    /**
     * Adds cards to the deck
     */
    operator fun invoke(vararg cards: T) = addCard(*cards)

    /**
     * Adds cards to the deck
     */
    operator fun invoke(card: Iterable<T>) = addCards(card)

    /**
     * Gets the card in the [index] of the deck
     * @throws DeckException if the [index] is outside of the deck's bounds
     */
    @Throws(DeckException::class)
    operator fun get(index: Int) = tryCatch("Index: $index, Size: $size") { deck[index] }

    /**
     * Gets a list from the deck between [range]
     * @throws DeckException if the [range] is outside the bounds of the deck
     */
    @Throws(DeckException::class)
    open operator fun get(range: IntRange) = tryCatch("Index: ${range.first} to ${range.last}, Size: $size") { deck.slice(range) }

    /**
     * Gets a list from the deck between [from] and [to]
     * @throws DeckException if the range is outside the bounds of the deck
     */
    @Throws(DeckException::class)
    open operator fun get(from: Int, to: Int) = tryCatch("Index: $from to $to, Size: $size") { deck.subList(from, to) }

    /**
     * Gets cards from [cards]
     */
    open operator fun get(vararg cards: T) = getCards(*cards)

    /**
     * Draws multiple Cards!
     * @throws DeckException if the card_games.Deck is Empty
     */
    @Throws(DeckException::class)
    open operator fun minus(amount: Int) = draw(amount)

    /**
     * Sets [index] of the deck with [card]
     */
    operator fun set(index: Int, card: T) = tryCatch("Index: $index, Size: $size") { deckOfCards[index] = card }

    /**
     * Adds [card] to this deck
     */
    operator fun plusAssign(card: T) = addCard(card).let { Unit }

    /**
     * Adds [cards] to this deck
     */
    operator fun plusAssign(cards: Iterable<T>) = addCards(cards).let { Unit }

    /**
     * Adds the cards from [deck] to this deck
     */
    operator fun plusAssign(deck: AbstractDeck<T>) = addDeck(deck).let { Unit }

    /**
     * Removes [card] from the deck if it's there
     */
    open operator fun minusAssign(card: T) = remove(card).let { Unit }

    /**
     * Draws a Card!
     * @throws DeckException if the Deck is Empty
     */
    @Throws(DeckException::class)
    open operator fun unaryMinus() = draw()

    /**
     * Returns an iterator over the elements of this object.
     */
    operator fun iterator() = deck.iterator()

    /**
     * checks if [card] is in this deck
     */
    operator fun contains(card: T) = card in deck

    /**
     * @see cutShuffle
     */
    open operator fun divAssign(cuts: Int) = cutShuffle(cuts)

    operator fun <T> Int.rangeTo(deck: AbstractDeck<T>) = deck.deck.subList(this, deck.size)

    override fun equals(other: Any?): Boolean {
        return if (other is AbstractDeck<*> && size == other.size) {
            for (thisCard in deckOfCards) {
                @Suppress("UNCHECKED_CAST")
                if (thisCard !in other as AbstractDeck<T>) return false
            }
            true
        } else false
    }

    /**
     * Splits the deck into [cuts] decks, shuffles each of them, then shuffles the order oof them all, then puts them back together
     */
    open fun cutShuffle(cuts: Int = 2) {
        val tempDeck = splitInto(cuts)
        deckOfCards.clear()
        deckOfCards.addAll(tempDeck.shuffled().flatMap(List<T>::shuffled))
    }

    /**
     * Cuts the deck in half then puts the bottom on the top
     */
    open fun cut() {
        val (top, bottom) = split()
        deckOfCards.clear()
        deckOfCards.addAll(listOf(bottom, top).flatten())
    }

    private fun split() = deckOfCards.subList(0, size / 2).toList() to deckOfCards.subList(size / 2, size).toList()
    private fun splitInto(cuts: Int) = deckOfCards.chunked(size / cuts)

    private fun <R, V> V.tryCatch(message: String?, block: (V) -> R) = try {
        block(this)
    } catch (e: Exception) {
        throw DeckException(message)
    }

    override fun hashCode(): Int = deckOfCards.hashCode()
}