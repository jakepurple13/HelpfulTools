package com.programmersbox.funutils

import com.programmersbox.funutils.cards.Card
import com.programmersbox.funutils.cards.Deck
import com.programmersbox.funutils.funutilities.SequenceMaker
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        //assertEquals(4, 2 + 2)
        Deck(Card.RandomCard)
        val f = SequenceMaker(1, 2, 3) {

        }
    }

    @Test
    fun deckPinchTest() {
        fun <T> pinchList(list: MutableList<T>) {
            val newList = list.pinch()
            println("$list -> $newList")
        }
        pinchList(mutableListOf(1, 2, 3, 4, 5, 6))
        pinchList(mutableListOf(1, 2, 3, 4, 5, 6, 7))
        pinchList(mutableListOf(9, 7, 5, 3, 1, 2, 4, 6, 8, 10))
        pinchList(mutableListOf(1, 1, 2, 1, 2))
        pinchList(mutableListOf<Int>())
        println("-".repeat(50))
        fun <T> pinchList2(list: MutableList<T>) {
            val newList = list.pinch2()
            println("$list -> $newList")
        }
        pinchList2(mutableListOf(1, 2, 3, 4, 5, 6))
        pinchList2(mutableListOf(1, 2, 3, 4, 5, 6, 7))
        pinchList2(mutableListOf(9, 7, 5, 3, 1, 2, 4, 6, 8, 10))
        pinchList2(mutableListOf(1, 1, 2, 1, 2))
        pinchList2(mutableListOf<Int>())
    }

    private fun <T> MutableList<T>.pinch(): List<T> {
        val newList = mutableListOf<T>()
        while (isNotEmpty()) {
            newList.add(0, removeAt(lastIndex))
            if (isNotEmpty()) newList.add(0, removeAt(0))
        }
        return newList
    }

    private tailrec fun <T> MutableList<T>.pinch2(newList: MutableList<T> = mutableListOf()): List<T> {
        if (isNotEmpty()) newList.add(0, removeAt(lastIndex))
        if (isNotEmpty()) newList.add(0, removeAt(0))
        return if (isEmpty()) newList else pinch2(newList)
    }
}