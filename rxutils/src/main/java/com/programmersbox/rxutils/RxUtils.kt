package com.programmersbox.rxutils

import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

/**
 * This will [Observable.subscribeOn] the io thread and [Observable.observeOn] the main thread
 */
fun <T> Observable<T>.ioMain(): Observable<T> = subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

operator fun <T> Observer<T>.invoke(item: T) = onNext(item)
operator fun <T> Observer<T>.invoke(throwable: Throwable) = onError(throwable)
operator fun <T> Observer<T>.invoke(d: Disposable) = onSubscribe(d)
operator fun <T> Observer<T>.invoke() = onComplete()

operator fun <T> Emitter<T>.invoke(item: T) = onNext(item)
operator fun <T> Emitter<T>.invoke(throwable: Throwable) = onError(throwable)
operator fun <T> Emitter<T>.invoke() = onComplete()

operator fun <T> SingleEmitter<T>.invoke(item: T) = onSuccess(item)
operator fun <T> SingleEmitter<T>.invoke(throwable: Throwable) = onError(throwable)

operator fun <T> MaybeEmitter<T>.invoke(item: T) = onSuccess(item)
operator fun <T> MaybeEmitter<T>.invoke(throwable: Throwable) = onError(throwable)
operator fun <T> MaybeEmitter<T>.invoke() = onComplete()

operator fun CompletableEmitter.invoke(throwable: Throwable) = onError(throwable)
operator fun CompletableEmitter.invoke() = onComplete()

/**
 * Does a [map]ping function with [apply] to modify the new element
 */
fun <T> Observable<T>.modify(block: (T) -> Unit): Observable<T> = map { it.apply(block) }

/**
 * Filters and emits items that are of [T]
 * @see Observable.filter
 */
@Suppress("UNCHECKED_CAST")
inline fun <reified T> Observable<*>.filterIsInstance(): Observable<T> = filter { it is T } as Observable<T>

/**
 * Filters items that are of [T]
 */
@Suppress("UNCHECKED_CAST")
fun <T : Any> Observable<*>.filterIsInstance(kClass: KClass<T>): Observable<T> = filter(kClass::isInstance) as Observable<T>

class BehaviorDelegate<T> internal constructor(private val subject: BehaviorSubject<T>) : ReadWriteProperty<Any?, T?> {
    override operator fun getValue(thisRef: Any?, property: KProperty<*>): T? = subject.value
    override operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T?) = value?.let(subject::onNext).let { Unit }
}

/**
 * Use this to link a variable to a [BehaviorSubject]
 */
fun <T> behaviorDelegate(subject: BehaviorSubject<T>) = BehaviorDelegate(subject)

/**
 * Use this to link a variable to a [BehaviorSubject]
 */
fun <T> BehaviorSubject<T>.toDelegate() = behaviorDelegate(this)

/**
 * An easy way to transform a list
 * calls
 * ```kotlin
 *      map { it.map(transform) }
 * ```
 */
fun <T, R> Observable<List<T>>.listMap(transform: (T) -> R): Observable<List<R>> = map { it.map(transform) }

/**
 * An easy way to transform a list
 * calls
 * ```kotlin
 *      map { it.map(transform) }
 * ```
 */
fun <T, R> Flowable<List<T>>.listMap(transform: (T) -> R) = map { it.map(transform) }

fun <T> Observable<T>.toLatestFlowable(): Flowable<T> = toFlowable(BackpressureStrategy.LATEST)
fun <T> Observable<T>.toBufferFlowable(): Flowable<T> = toFlowable(BackpressureStrategy.BUFFER)
fun <T> Observable<T>.toDropFlowable(): Flowable<T> = toFlowable(BackpressureStrategy.DROP)
fun <T> Observable<T>.toErrorFlowable(): Flowable<T> = toFlowable(BackpressureStrategy.ERROR)
fun <T> Observable<T>.toMissingFlowable(): Flowable<T> = toFlowable(BackpressureStrategy.MISSING)
