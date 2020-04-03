package com.programmersbox.testingplaygroundapp

import com.programmersbox.testingplaygroundapp.cardgames.blackjack.BlackjackActivity

enum class Games(val text: String, val clazz: Class<*>) {
    BLACKJACK("Blackjack", BlackjackActivity::class.java)
}