package com.programmersbox.helpfultools

import com.programmersbox.helpfultools.broadcast.BroadcastActivity

enum class TypeToGo(val text: String, val clazz: Class<*>) {
    BLACKJACK("Sequence Maker", MoreActivity::class.java),
    BROADCAST("Broadcast Receivers", BroadcastActivity::class.java)
}