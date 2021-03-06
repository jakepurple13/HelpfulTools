package com.programmersbox.rxutils

import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import org.junit.Test
import kotlin.random.Random

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    sealed class InstanceFilter {
        object EmptyInstance : InstanceFilter()
        object Filter : InstanceFilter()
    }

    @Test
    fun instanceFilterTest() {
        val publish = PublishSubject.create<InstanceFilter>()
        publish
            .filterIsInstance<InstanceFilter.EmptyInstance>()
            //.filterIsInstance(InstanceFilter.EmptyInstance::class)
            .doOnError { println(it) }
            .subscribe { println(it) }

        publish(InstanceFilter.EmptyInstance)
        publish(InstanceFilter.Filter)

        println("-----")

        publish
            //.filterIsInstance<InstanceFilter.EmptyInstance>()
            .filterIsInstance(InstanceFilter.Filter::class)
            .doOnError { println(it) }
            .subscribe { println(it) }

        publish(InstanceFilter.EmptyInstance)
        publish(InstanceFilter.Filter)
    }

    @Test
    fun addition_isCorrect() {
        val publish = PublishSubject.create<String>()
        publish
            .doOnError { println(it) }
            .subscribe { println(it) }
        publish("Hello")
        publish.onNext("World")
        publish.modify { }.subscribe()
        publish("!!!")
    }

    @Test
    fun otherRxTest() {
        val publish = BehaviorSubject.create<String>()
        publish
            .doOnError { println(it) }
            .subscribe { println(it) }
        var item: String? by behaviorDelegate(publish)
        var item2: String? by publish.toDelegate()
        println(item)
        item = "Hello"
        item = "World"
        println(item)
    }

    @Test
    fun modifyTest() {
        data class RxTest(var text: String)

        val publish = PublishSubject.create<RxTest>()
        publish
            .doOnComplete { println("Done") }
            .doOnError { println(it) }
            .subscribe { println(it) }
        publish(RxTest("Hello"))
        publish.onNext(RxTest("World"))
        publish
            .modify { it.text = "Hello World" }
            .doOnComplete { System.err.println("Done") }
            .subscribe { System.err.println(it) }
        publish(RxTest("!!!")) //calls onNext
        if (Random.nextBoolean())
            publish() //calls onComplete
        else
            publish(Throwable("Hello There")) //calls onError

    }
}