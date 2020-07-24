package com.programmersbox.funutils

import com.programmersbox.funutils.cards.Card
import com.programmersbox.funutils.cards.Deck
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
        val d = Deck(Card.RandomCard)
    }
}