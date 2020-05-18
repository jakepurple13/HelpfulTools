package com.programmersbox.testingplaygroundapp.cardgames.uno

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.programmersbox.dragswipe.Direction
import com.programmersbox.dragswipe.DragSwipeAdapter
import com.programmersbox.dragswipe.DragSwipeUtils
import com.programmersbox.funutils.cards.Card
import com.programmersbox.funutils.cards.Suit
import com.programmersbox.loggingutils.Loged
import com.programmersbox.loggingutils.f
import com.programmersbox.testingplaygroundapp.R
import com.programmersbox.testingplaygroundapp.cardgames.blackjack.VerticalOverlapDecoration
import com.programmersbox.testingplaygroundapp.cardgames.getImage
import kotlinx.android.synthetic.main.activity_uno.*
import kotlinx.android.synthetic.main.card_item.view.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class UnoActivity : AppCompatActivity() {

    private val adapter = CardAdapter(true)
    private val player = UnoPlayer("Player")
    private val aiOne = UnoPlayer("AI Dusty")
    private val aiTwo = UnoPlayer("AI Pudding")
    private val aiThree = UnoPlayer("AI Faye Kinnit")
    private val game = UnoGame(player, aiOne, aiTwo, aiThree)
    private val backCard = Card(16, Suit.SPADES)

    private val oneAdapter = CardAdapter()
    private val twoAdapter = CardAdapter()
    private val threeAdapter = CardAdapter()

    @SuppressLint("SetTextI18n")
    @ExperimentalStdlibApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_uno)

        game.setListener {
            cardPlayed { played, player, card ->
                if (played) {
                    when (player) {
                        this@UnoActivity.player -> adapter
                        aiOne -> oneAdapter
                        aiTwo -> twoAdapter
                        aiThree -> threeAdapter
                        else -> null
                    }?.removeItem(card)
                    setUnoCard(card)
                    computerMove(player)
                }
                adapterModify { notifyDataSetChanged() }
                playerPlaying.text = "${game.currentPlayer.name}'s\nturn"
            }
            wild {
                if (it != player) it.hand.filter { it.color != UnoColor.BLACK }.randomOrNull()?.color ?: UnoColor.playableColors.random()
                else wildCard()
            }
            orderChanged { Loged.f("Order Changed") }
            addCardsToDeck { Loged.f("Cards added to deck") }
            cardDrawn {
                adapter.setListNotify(player.hand)
                oneAdapter.setListNotify(aiOne.hand)
                twoAdapter.setListNotify(aiTwo.hand)
                threeAdapter.setListNotify(aiThree.hand)
                adapterModify { notifyDataSetChanged() }
            }
        }

        playerCards.adapter = adapter
        DragSwipeUtils.setDragSwipeUp(adapter, playerCards, listOf(Direction.START, Direction.END))
        adapter.addItems(player.hand)

        setUpComAdapters()

        game.topCard?.let(this::setUnoCard)

        currentCard.setOnClickListener {
            if (game.currentPlayer == player) game.noCardDraw()
        }

        playerPlaying.text = "${game.currentPlayer.name}'s\nturn"

    }

    private fun computerMove(currentPlayer: UnoPlayer) {

        fun move() = currentPlayer.hand.find(game::isPlayable)?.let(game::playCard) ?: game.noCardDraw()

        GlobalScope.launch {
            runOnUiThread {
                when (currentPlayer) {
                    aiOne, aiTwo, aiThree -> move()
                    else -> Unit
                }
            }
            delay(500)
        }
    }

    private fun adapterModify(block: CardAdapter.() -> Unit) {
        adapter.block()
        oneAdapter.block()
        twoAdapter.block()
        threeAdapter.block()
    }

    private fun setUpComAdapters() {
        comOne.adapter = oneAdapter
        comTwo.adapter = twoAdapter
        comThree.adapter = threeAdapter
        val bitmap = BitmapFactory.decodeResource(resources, backCard.getImage(this))
        comOne.addItemDecoration(VerticalOverlapDecoration((-bitmap.height / 1.5).toInt()))
        comThree.addItemDecoration(VerticalOverlapDecoration((-bitmap.height / 1.5).toInt()))

        oneAdapter.addItems(aiOne.hand)
        twoAdapter.addItems(aiTwo.hand)
        threeAdapter.addItems(aiThree.hand)
    }

    private fun wildCard(): UnoColor {
        MaterialAlertDialogBuilder(this@UnoActivity)
            .setItems(UnoColor.playableColors.map(UnoColor::name).toTypedArray())
            { d: DialogInterface, color: Int -> game.wildCardTwo(UnoColor.playableColors[color]) }
            .setTitle("Choose a color")
            .show()
        return UnoColor.BLACK
    }

    private fun setUnoCard(card: UnoCard) {
        currentCard.setColorFilter(card.color.getHexColor)
        currentCardInfo.text = card.type
    }

    inner class CardAdapter(private val isPlayer: Boolean = false) :
        DragSwipeAdapter<UnoCard, ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
            ViewHolder(layoutInflater.inflate(R.layout.card_item, parent, false))

        override fun ViewHolder.onBind(item: UnoCard, position: Int) {
            itemView.cardImage.setImageResource(R.drawable.b1fv)
            if (isPlayer) {
                itemView.cardImage.setColorFilter(item.color.getHexColor)
                itemView.cardImage.setBackgroundResource(R.drawable.border)
                itemView.cardText.text = item.type
                itemView.cardText.isClickable = false
                itemView.setOnClickListener { game.playCard(item) }
            } else {
                itemView.cardText.text = "$position"
            }
        }

        fun removeItem(item: UnoCard) = dataList.indexOf(item).let { if (it != -1) removeItem(it) }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

}