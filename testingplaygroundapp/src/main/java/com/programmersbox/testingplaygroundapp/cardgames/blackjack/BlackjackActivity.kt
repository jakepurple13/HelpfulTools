package com.programmersbox.testingplaygroundapp.cardgames.blackjack

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.programmersbox.dragswipe.*
import com.programmersbox.flowutils.FlowItem
import com.programmersbox.flowutils.clicks
import com.programmersbox.flowutils.plusAssign
import com.programmersbox.funutils.cards.Card
import com.programmersbox.funutils.cards.Deck
import com.programmersbox.funutils.cards.Suit
import com.programmersbox.testingplaygroundapp.R
import com.programmersbox.testingplaygroundapp.cardgames.getImage
import kotlinx.android.synthetic.main.activity_blackjack.*
import kotlinx.android.synthetic.main.card_item.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class BlackjackActivity : AppCompatActivity() {

    private val deck = Deck.defaultDeck()
    private val dealerCardAdapter = CardAdapter(mutableListOf())
    private val playerCardAdapter = CardAdapter(mutableListOf())
    private val cardsWonAdapter = CardAdapter(mutableListOf())
    private val backCard = Card(16, Suit.SPADES)
    private val winCount = FlowItem(0)
    private val loseCount = FlowItem(0)
    private val tieCount = FlowItem(0)

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blackjack)

        combine(winCount.flow, loseCount.flow, tieCount.flow) { arr -> arr }
            .collectOnUi { playInfo.text = "W: %d - L: %d - T: %d".format(*it) }

        deck.shuffle()
        deck.addDeckListener {
            onDraw { _, size ->
                if (size <= 5) {
                    deck(Deck.defaultDeck())
                    deck.trueRandomShuffle()
                }
                deckCount.text = "$size cards left"
            }
        }

        adapterRecyclerViewSetup(dealerCards, dealerCardAdapter)
        adapterRecyclerViewSetup(cardsWon, cardsWonAdapter)
        adapterRecyclerViewSetup(playerCards, playerCardAdapter)

        cardsWonAdapter += backCard

        hitButton
            .clicks()
            .collectOnUi { playerDraw() }

        stayButton
            .clicks()
            .collectOnUi {
                enableControls(false)
                startDealerPlay()
            }

        resetField()
    }

    private fun enableControls(enable: Boolean) {
        hitButton.isEnabled = enable
        stayButton.isEnabled = enable
    }

    private fun CardAdapter.draw() {
        if (this[0] == backCard) this[0] = deck.draw()
        else this += deck.draw()
    }

    private fun startDealerPlay() {
        GlobalScope.launch {
            while (dealerCardAdapter.total() <= 16) {
                runOnUiThread { dealerDraw() }
                delay(1000)
            }
            if (dealerCardAdapter.total() > 21) {
                bust(winCount, "Dealer")
                addCardsToWin()
            } else runOnUiThread { findWinner() }
        }
    }

    enum class Winner { WIN, LOSE, TIE }

    private fun findWinner() {
        GlobalScope.launch {
            val playerTotal = playerCardAdapter.total()
            val dealerTotal = dealerCardAdapter.total()
            val item = when {
                playerTotal in (dealerTotal + 1)..21 -> Winner.WIN
                dealerTotal in (playerTotal + 1)..21 -> Winner.LOSE
                playerTotal == dealerTotal -> Winner.TIE
                else -> Winner.TIE
            }

            suspend fun winCheck(item: FlowItem<Int>, text: String) {
                runOnUiThread { playInfo.text = text }
                delay(1000)
                runOnUiThread { item += 1 }
            }

            when (item) {
                Winner.WIN -> winCheck(winCount, "You Won").also { addCardsToWin() }
                Winner.LOSE -> winCheck(loseCount, "You Lose")
                Winner.TIE -> winCheck(tieCount, "You Tied")
            }
            runOnUiThread { resetField() }
        }
    }

    private fun addCardsToWin() = runOnUiThread {
        if (cardsWonAdapter[0] == backCard) cardsWonAdapter.removeItem(0)
        cardsWonAdapter.addItems(playerCardAdapter.dataList)
        cardsWonAdapter.addItems(dealerCardAdapter.dataList)
    }

    @SuppressLint("SetTextI18n")
    private fun bust(item: FlowItem<Int>, text: String) {
        GlobalScope.launch {
            runOnUiThread {
                enableControls(false)
                playInfo.text = "$text Bust"
            }
            delay(1000)
            runOnUiThread {
                item += 1
                resetField()
            }
        }
    }

    private fun resetField() {
        playerCardAdapter.dataList.clear()
        playerCardAdapter.notifyDataSetChanged()
        playerCardAdapter += backCard
        dealerCardAdapter.dataList.clear()
        dealerCardAdapter.notifyDataSetChanged()
        dealerCardAdapter += backCard
        GlobalScope.launch {
            runOnUiThread { playerDraw() }
            delay(200)
            runOnUiThread { dealerDraw() }
            delay(200)
            runOnUiThread { playerDraw() }
            runOnUiThread { enableControls(true) }
        }
    }

    private fun playerDraw() {
        playerCardAdapter.draw()
        val playerSum = playerCardAdapter.total()
        playerInfo.text = "$playerSum"
        if (playerSum >= 22) bust(loseCount, "You")
    }

    private fun dealerDraw() {
        dealerCardAdapter.draw()
        dealerInfo.text = "${dealerCardAdapter.total()}"
    }

    private fun CardAdapter.total() = dataList.sortedByDescending(Card::valueTen).fold(0) { acc, card ->
        acc + if (card.value == 1 && acc + 11 < 22) 11 else if (card.value == 1) 1 else card.valueTen
    }

    private fun adapterRecyclerViewSetup(recyclerView: RecyclerView, adapter: CardAdapter) {
        val bitmap = BitmapFactory.decodeResource(resources, backCard.getImage(this))
        recyclerView.addItemDecoration(OverlapDecoration((-bitmap.width / 1.5).toInt()))
        recyclerView.adapter = adapter
        DragSwipeUtils.setDragSwipeUp(adapter, recyclerView, listOf(Direction.START, Direction.END))
    }

    inner class CardAdapter(dataList: MutableList<Card>) : DragSwipeAdapter<Card, ViewHolder>(dataList) {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
            ViewHolder(layoutInflater.inflate(R.layout.card_item, parent, false))

        override fun ViewHolder.onBind(item: Card, position: Int) {
            itemView.cardImage.setImageResource(item.getImage(this@BlackjackActivity))
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}

class OverlapDecoration(private var horizontalOverlap: Int = -200) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val itemPosition = parent.getChildAdapterPosition(view)
        if (itemPosition == 0) return
        outRect.set(horizontalOverlap, 0, 0, 0)
    }
}

private fun <T> Flow<T>.collectOnUi(action: (T) -> Unit) = GlobalScope.launch { collect { GlobalScope.launch(Dispatchers.Main) { action(it) } } }
