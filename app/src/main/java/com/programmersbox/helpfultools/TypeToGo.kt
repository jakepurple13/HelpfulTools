package com.programmersbox.helpfultools

import com.programmersbox.helpfultools.broadcast.BroadcastActivity
import com.programmersbox.helpfultools.contextutility.ContextActivity

enum class TypeToGo(val text: String, val clazz: Class<*>) {
    BLACKJACK("Sequence Maker", MoreActivity::class.java),
    BROADCAST("Broadcast Receivers", BroadcastActivity::class.java),
    CONTEXT("Context Utils", ContextActivity::class.java)
}