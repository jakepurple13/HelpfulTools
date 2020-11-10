package com.programmersbox.flowutils

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.selects.select
import org.junit.After
import org.junit.Test
import kotlin.random.Random
import kotlin.system.measureTimeMillis

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    private val disposable = JobDisposable()

    @After
    fun after() {
        println(disposable.size)
        println(disposable)
        disposable.dispose()
    }

    @Test
    fun flowTest() {
        println(FlowItem(100))
    }

    @Test
    fun stateFlowIItemTest() = runBlocking {
        flowItemTest()
        stateFlowTest()
    }

    val item: FlowItem<Int> = 1.asFlowItem()

    @Test
    fun flowItemTest() = runBlocking {
        val f = measureTimeMillis {
            item.collect { println(it) }.addTo(disposable)
            delay(1000)
            item(10)
            item.setValue(100)
            delay(1000)
            item.now()
            disposable += GlobalScope.launch { item.flow.collect { println("From item.flow $it") } }
            item(20)
            delay(1000)
            item.setValue(60)
            delay(1000)
            println(item())
        }
        println("Time: $f")
    }

    val item2 = MutableStateFlow(1)

    @Test
    fun stateFlowTest() = runBlocking {
        val f = measureTimeMillis {
            GlobalScope.launch { item2.onStart { emit(item2.value) }.collect { println(it) } }.addTo(disposable)
            delay(1000)
            item2(10)
            item2(100)
            delay(1000)
            item2.now()
            GlobalScope.launch { item2.onStart { emit(item2.value) }.collect { println("From item.flow $it") } }.addTo(disposable)
            item2(20)
            delay(1000)
            item2(60)
            delay(1000)
            println(item2())
        }
        println("Time: $f")
    }

    @Test
    fun tickerTest() = runBlocking {
        timerFlow(500) { println("Time passed") }
        delay(2500)
    }

    private val booleanItem: FlowItem<Boolean> = true.asFlowItem()
    private val intItem: FlowItem<Int> = 1.asFlowItem()
    private val longItem: FlowItem<Long> = 1L.asFlowItem()
    private val doubleItem: FlowItem<Double> = 1.0.asFlowItem()
    private val floatItem: FlowItem<Float> = 1F.asFlowItem()
    private val shortItem: FlowItem<Short> = 1.toShort().asFlowItem()

    private fun newLine(category: String = "") = println("$category${"-".repeat(50)}")

    @Test
    fun flowItemOperatorTest() = runBlocking {
        booleanItem.collect { println("Boolean: $it") }.addTo(disposable)
        intItem.collect { println("Int $it") }.addTo(disposable)
        longItem.collect { println("Long $it") }.addTo(disposable)
        doubleItem.collect { println("Double $it") }.addTo(disposable)
        floatItem.collect { println("Float $it") }.addTo(disposable)
        shortItem.collect { println("Short $it") }.addTo(disposable)
        delay(1000)
        !booleanItem
        newLine("Plus")
        intItem += 5
        longItem += 5
        doubleItem += 5.0
        floatItem += 5F
        shortItem += 5.toShort()
        delay(1000)
        newLine("Times")
        intItem *= 5
        longItem *= 5
        doubleItem *= 5.0
        floatItem *= 5F
        shortItem *= 5.toShort()
        delay(1000)
        newLine("Minus")
        intItem -= 5
        longItem -= 5
        doubleItem -= 5.0
        floatItem -= 5F
        shortItem -= 5.toShort()
        delay(1000)
        newLine("Divide")
        intItem /= 5
        longItem /= 5
        doubleItem /= 5.0
        floatItem /= 5F
        shortItem /= 5.toShort()
        delay(1000)
        newLine("Mod")
        intItem %= 5
        longItem %= 5
        doubleItem %= 5.0
        floatItem %= 5F
        shortItem %= 5.toShort()
        delay(1000)
        println(booleanItem)
        println(intItem)
        println(longItem)
        println(doubleItem)
        println(floatItem)
        println(shortItem)
    }

    private val mutableListItem = FlowItem(mutableListOf(1, 2, 3, 4, 5))

    @Test
    fun flowItemListTest() = runBlocking {
        newLine("List")
        val listItem = FlowItem(listOf(1, 2, 3, 4, 5))
        println(listItem[3])
        newLine("Mutable")
        mutableListItem.collect { println("MutableList(${mutableListItem.size}): $it") }.addTo(disposable)
        delay(1000)
        println(mutableListItem[2])
        mutableListItem[2] = 10
        println(mutableListItem[2])
        delay(1000)
        for (i in mutableListItem) println(i)
        delay(1000)
        println(10 in mutableListItem)
        delay(1000)
        mutableListItem += 50
        delay(1000)
        mutableListItem.add(20)
        delay(1000)
    }

    @Test
    fun jobTesting() = runBlocking {
        var loadMarkersJob by JobReset()

        for (i in 0..10) {
            println("Before Get/Set")
            try {
                loadMarkersJob = methodReturningJob()
            } catch (e: Exception) {
                continue
            }
            println("Start Loop Delay")
            val f = Random.nextInt(1, 10) * 1000L
            println("Delay: $f")
            delay(f)
            println("End Loop Delay")
        }
        loadMarkersJob?.cancel()

        Unit
    }

    @Test
    fun broadcastChannelQuickCheck() = runBlocking {

        val b = BroadcastChannel<Int>(Channel.CONFLATED)

        b.sendBlocking(4)
        val c = b.asFlow()

        launch {
            c.collect { println(it) }
        }.addTo(disposable)

        b.sendBlocking(5)

        launch {
            c.collect { println(it) }
        }.addTo(disposable)

        b.sendBlocking(6)

    }.let { Unit }

    private fun methodReturningJob() = GlobalScope.launch {
        println("Before Delay")
        delay(5000)
        println("After Delay")
        throw Exception("Finished")
    }

    @Test
    fun coroutineSelect() = runBlocking {

        fun CoroutineScope.fizz() = produce<String> {
            while (true) { // sends "Fizz" every 300 ms
                delay(300)
                send("Fizz")
            }
        }

        fun CoroutineScope.buzz() = produce<String> {
            while (true) { // sends "Buzz!" every 500 ms
                delay(500)
                send("Buzz!")
            }
        }

        suspend fun selectFizzBuzz(fizz: ReceiveChannel<String>, buzz: ReceiveChannel<String>) = select<Unit> {
            // <Unit> means that this select expression does not produce any result
            fizz.onReceive { value ->  // this is the first select clause
                println("fizz -> '$value'")
            }
            buzz.onReceive { value ->  // this is the second select clause
                println("buzz -> '$value'")
            }
        }

        val fizz = fizz()
        val buzz = buzz()
        repeat(7) {
            selectFizzBuzz(fizz, buzz)
        }
        coroutineContext.cancelChildren() // cancel fizz & buzz coroutines

        /*println("-".repeat(50))

        val flow1 = flow<String> {
            while (true) { // sends "Fizz" every 300 ms
                delay(300)
                emit("Fizz")
            }
        }

        val flow2 = flow<String> {
            while (true) { // sends "Buzz!" every 500 ms
                delay(500)
                emit("Buzz!")
            }
        }

        repeat(7) {
            selectFairChannel(
                flow1 to { c -> println(c) },
                flow2 to { c -> println(c) }
            )
        }

        coroutineContext.cancelChildren()*/

        /*fun CoroutineScope.switchMapDeferreds(input: ReceiveChannel<Deferred<String>>) = produce<String> {
            var current = input.receive() // start with first received deferred value
            while (isActive) { // loop while not cancelled/closed
                val next = select<Deferred<String>?> { // return next deferred value from this select or null
                    input.onReceiveOrNull { update ->
                        update // replaces next value to wait
                    }
                    current.onAwait { value ->
                        send(value) // send value that current deferred has produced
                        input.receiveOrNull() // and use the next deferred from the input channel
                    }
                }
                if (next == null) {
                    println("Channel was closed")
                    break // out of loop
                } else {
                    current = next
                }
            }
        }

        fun CoroutineScope.asyncString(str: String, time: Long) = async {
            delay(time)
            str
        }

        val chan = Channel<Deferred<String>>() // the channel for test
        launch { // launch printing coroutine
            for (s in switchMapDeferreds(chan))
                println(s) // print each received string
        }
        chan.send(asyncString("BEGIN", 100))
        delay(200) // enough time for "BEGIN" to be produced
        chan.send(asyncString("Slow", 500))
        delay(100) // not enough time to produce slow
        chan.send(asyncString("Replace", 100))
        delay(500) // give it time before the last one
        chan.send(asyncString("END", 500))
        delay(1000) // give it time to process
        chan.close() // close the channel ...
        delay(500) // and wait some time to let it finish*/

    }
}