package com.programmersbox.helpfulutils

import kotlin.random.Random

/**
 * a vararg version of [MutableList.addAll]
 */
fun <T> MutableList<T>.addAll(vararg args: T) = addAll(args)

/**
 * Finds similarities between two lists based on a predicate
 */
fun <T, R> List<T>.intersect(uList: List<R>, filterPredicate: (T, R) -> Boolean) = filter { m -> uList.any { filterPredicate(m, it) } }

/**
 * randomly removes one element
 */
fun <T> MutableList<T>.randomRemove(): T = removeAt(Random.nextInt(size))

/**
 * removes a random element based on a [predicate]
 * @throws NoSuchElementException if no elements match the [predicate]
 */
fun <T> MutableList<T>.randomRemove(predicate: (T) -> Boolean): T = removeAt(indexOf(filter(predicate).random()))

/**
 * get a random element based on a [predicate]
 * @throws NoSuchElementException if no elements match the [predicate]
 */
fun <T> Iterable<T>.random(predicate: (T) -> Boolean) = filter(predicate).random()