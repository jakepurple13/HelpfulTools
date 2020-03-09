package com.programmersbox.rxutils

import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

/**
 * This will [Observable.subscribeOn] the io thread and [Observable.observeOn] the main thread
 */
fun <T> Observable<T>.ioMain(): Observable<T> = subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

operator fun <T> Observer<T>.invoke(item: T) = onNext(item)
operator fun <T> Observer<T>.invoke(throwable: Throwable) = onError(throwable)
operator fun <T> Observer<T>.invoke(d: Disposable) = onSubscribe(d)
operator fun <T> Observer<T>.invoke() = onComplete()