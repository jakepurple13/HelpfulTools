package com.programmersbox.rxutils

import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * This will [Observable.subscribeOn] the io thread and [Observable.observeOn] the main thread
 */
fun <T> Observable<T>.ioMain(): Observable<T> = subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

operator fun <T> Observer<T>.invoke(item: T) = onNext(item)
operator fun <T> Observer<T>.invoke(throwable: Throwable) = onError(throwable)
operator fun <T> Observer<T>.invoke(d: Disposable) = onSubscribe(d)
operator fun <T> Observer<T>.invoke() = onComplete()

/**
 * Does a [map]ping function with [apply] to modify the new element
 */
fun <T> Observable<T>.modify(block: (T) -> Unit): Observable<T> = map { it.apply(block) }

class BehaviorDelegate<T> internal constructor(private val subject: BehaviorSubject<T>) : ReadWriteProperty<Nothing?, T?> {
    override operator fun getValue(thisRef: Nothing?, property: KProperty<*>): T? = subject.value
    override operator fun setValue(thisRef: Nothing?, property: KProperty<*>, value: T?) = value?.let(subject::onNext).let { Unit }
}

/**
 * Use this to link a variable to a [BehaviorSubject]
 */
fun <T> behaviorDelegate(subject: BehaviorSubject<T>) = BehaviorDelegate(subject)

/**
 * An easy way to transform a list
 * calls map { it.map(transform) }
 */
fun <T, R> Observable<List<T>>.listMap(transform: (T) -> R): Observable<List<R>> = map { it.map(transform) }

/**
 * An easy way to transform a list
 * calls map { it.map(transform) }
 */
fun <T, R> Flowable<List<T>>.listMap(transform: (T) -> R) = map { it.map(transform) }

fun <T> Observable<T>.toLatestFlowable(): Flowable<T> = toFlowable(BackpressureStrategy.LATEST)
fun <T> Observable<T>.toBufferFlowable(): Flowable<T> = toFlowable(BackpressureStrategy.BUFFER)
fun <T> Observable<T>.toDropFlowable(): Flowable<T> = toFlowable(BackpressureStrategy.DROP)
fun <T> Observable<T>.toErrorFlowable(): Flowable<T> = toFlowable(BackpressureStrategy.ERROR)
fun <T> Observable<T>.toMissingFlowable(): Flowable<T> = toFlowable(BackpressureStrategy.MISSING)
