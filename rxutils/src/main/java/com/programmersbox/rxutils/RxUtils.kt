package com.programmersbox.rxutils

import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

/**
 * This will [Observable.subscribeOn] the io thread and [Observable.observeOn] the main thread
 */
fun <T> Observable<T>.ioMain(): Observable<T> = observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())

operator fun <T> Observer<T>.invoke(item: T) = onNext(item)
operator fun <T> Observer<T>.invoke(throwable: Throwable) = onError(throwable)
operator fun <T> Observer<T>.invoke(d: Disposable) = onSubscribe(d)
operator fun <T> Observer<T>.invoke() = onComplete()

/**
 * Does a [map]ping function with [apply] to modify the new element
 */
fun <T> Observable<T>.modify(block: (T) -> Unit): Observable<T> = map { it.apply(block) }

/**
 * An easy way to set up onNext, onError, and onComplete
 */
fun <T> Observable<T>.build(builder: ObservableBuilder<T>.() -> Unit): Observable<T> = ObservableBuilder<T>().apply(builder).build(this)

@DslMarker
annotation class ObservableBuildMarker

@ObservableBuildMarker
class ObservableBuilder<T> {

    private var onNext: (T) -> Unit = {}

    @ObservableBuildMarker
    fun onNext(block: (T) -> Unit) = run { onNext = block }

    private var onError: (Throwable) -> Unit = {}

    @ObservableBuildMarker
    fun onError(block: (Throwable) -> Unit) = run { onError = block }

    private var onComplete: () -> Unit = {}

    @ObservableBuildMarker
    fun onComplete(block: () -> Unit) = run { onComplete = block }

    internal fun build(observe: Observable<T>) = observe.doOnNext(onNext).doOnError(onError).doOnComplete(onComplete)

}