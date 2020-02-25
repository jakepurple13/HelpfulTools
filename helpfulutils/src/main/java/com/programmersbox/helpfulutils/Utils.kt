package com.programmersbox.helpfulutils

inline fun <T> T.whatIf(
    given: Boolean?,
    whatIfTrue: T.() -> Unit,
    whatIfFalse: T.() -> Unit
) = apply { if (given == true) whatIfTrue() else whatIfFalse() }

inline fun <T> T.whatIf(
    given: Boolean?,
    whatIfTrue: T.() -> Unit
) = apply { if (given == true) whatIfTrue() }

inline fun <T, R> T.whatIfNotNull(
    given: R?,
    whatIfNotNull: T.(R) -> Unit
) = apply { given?.let { whatIfNotNull(it) } }

fun <T> MutableList<T>.addAll(vararg args: T) = addAll(args)
