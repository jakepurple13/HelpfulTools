package com.programmersbox.testingplaygroundapp

import com.programmersbox.testingplaygroundapp.cardgames.blackjack.BlackjackActivity
import com.programmersbox.testingplaygroundapp.cardgames.uno.UnoActivity

enum class Games(val text: String, val clazz: Class<*>) {
    BLACKJACK("Blackjack", BlackjackActivity::class.java),
    UNO("Uno", UnoActivity::class.java),
    CHECKBOX("CheckBox", CheckboxTestActivity::class.java),
    CUSTOM_VIEW("Custom Views", CustomViewActivity::class.java)
}