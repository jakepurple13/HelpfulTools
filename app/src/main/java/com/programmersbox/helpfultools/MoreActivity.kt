package com.programmersbox.helpfultools

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.programmersbox.flowutils.clicks
import com.programmersbox.flowutils.collectOnUi
import com.programmersbox.funutils.funutilities.SequenceListener
import com.programmersbox.funutils.funutilities.SequenceMaker
import com.programmersbox.funutils.funutilities.TimedSequenceMaker
import kotlinx.android.synthetic.main.activity_more.*
import kotlinx.coroutines.flow.map

class MoreActivity : AppCompatActivity() {

    private val sequenceList = listOf(Directions.UP, Directions.DOWN, Directions.LEFT, Directions.RIGHT)
    private val achieved = { Toast.makeText(this, "You did it!", Toast.LENGTH_SHORT).show() }
    private val sequenceReset = { Toast.makeText(this, "Sequenced Reset", Toast.LENGTH_SHORT).show() }

    private val sequenceMaker = SequenceMaker(sequenceList, object : SequenceListener {
        override fun onAchieved() = achieved()
        override fun onReset() = sequenceReset()
    })
    private val timedSequenceMaker = TimedSequenceMaker(sequenceList, 2000, object : SequenceListener {
        override fun onAchieved() = achieved()
        override fun onReset() = sequenceReset()
    })

    private var sequence: SequenceMaker<Directions>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_more)

        useSequence
            .clicks()
            .map { sequence = sequenceMaker }
            .collectOnUi { sequence?.resetSequence() }

        useTimed
            .clicks()
            .map { sequence = timedSequenceMaker }
            .collectOnUi { sequence?.resetSequence() }

        resetSequence
            .clicks()
            .collectOnUi { sequence?.resetSequence() }

        arrowSetup(
            upArrow to Directions.UP,
            downArrow to Directions.DOWN,
            leftArrow to Directions.LEFT,
            rightArrow to Directions.RIGHT
        )

    }

    private fun arrowSetup(vararg pairs: Pair<Button, Directions>) =
        pairs.forEach { pair -> pair.first.clicks().collectOnUi { nextItem(pair.second) } }

    private fun nextItem(directions: Directions) = sequence?.add(directions) ?: Unit

    enum class Directions { UP, DOWN, LEFT, RIGHT }

}