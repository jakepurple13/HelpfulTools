package com.programmersbox.rxutils

import io.reactivex.subjects.PublishSubject
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        val publish = PublishSubject.create<String>()
        publish
            //.ioMain()
            .doOnError { println(it) }
            .subscribe { println(it) }
        publish("Hello")
        publish(Throwable("Hello There"))
        publish()
        publish.onNext("World")
    }
}