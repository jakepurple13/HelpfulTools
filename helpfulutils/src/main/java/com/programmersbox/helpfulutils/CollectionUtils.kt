package com.programmersbox.helpfulutils

import kotlin.random.Random

/**
 * a vararg version of [MutableList.addAll]
 */
fun <T> MutableCollection<T>.addAll(vararg args: T) = addAll(args)

/**
 * Finds similarities between two lists based on a predicate
 */
fun <T, R> Iterable<T>.intersect(uList: Iterable<R>, filterPredicate: (T, R) -> Boolean) = filter { m -> uList.any { filterPredicate(m, it) } }

/**
 * Another way to call the [Iterable.intersect] method
 * @see intersect
 */
fun <T, R> Pair<Iterable<T>, Iterable<R>>.intersect(predicate: (T, R) -> Boolean) = first.intersect(second, predicate)

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

/**
 * Creates a list of [amount] size
 * Useful for random information
 */
fun <T> sizedListOf(amount: Int = 1, item: (Int) -> T) = mutableListOf<T>().apply { repeat(amount) { this += item(it) } }