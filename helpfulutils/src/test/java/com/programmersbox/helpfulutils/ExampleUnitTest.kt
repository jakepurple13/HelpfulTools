package com.programmersbox.helpfulutils

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test
import java.text.SimpleDateFormat
import java.time.*
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalUnit
import java.util.*
import kotlin.random.Random
import kotlin.system.measureNanoTime
import kotlin.system.measureTimeMillis
import kotlin.time.ExperimentalTime
import kotlin.time.days
import kotlin.time.hours
import kotlin.time.measureTime
import kotlin.time.minutes

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    data class UtilObject(var string: String, var int: Int?)

    @Test
    fun whatIfTest() {
        val f = 0 through 5
        val intValue = if (Random.nextBoolean()) Random.nextInt(0, 20) else null
        val util = UtilObject("Hello", intValue)
        println(util)

        util
            .whatIf(util.int in 0..5) { string = "World" }
            .whatIf(util.int in 5..10, whatIfTrue = { string = "Goodbye" }, whatIfFalse = { string = "World" })
            .whatIfNotNull(util.int) { string = "Its not null $it" }

        println(util)
    }

    @Test
    fun mutableTest() {
        val list = mutableListOf<Int>()
        list.addAll(1, 3, 5, 6, 7, 8)
        println(list)
        println(list.randomRemove())
        println(list)
        println(list.randomRemove { it % 2 == 1 })
        println(list)
        println(list.random { it % 2 == 0 })
        val list2 = listOf(1, 2, 4, 8)
        val list3 = list.intersect(list2) { l1, l2 -> l1 == l2 }
        println(list3)
        val pair = list to list2
        println(pair.intersect { i, i2 -> i == i2 })
        println(sizedListOf(5) { Random.nextString(5) })
        println(list)
        println(list.lastWithIndex)
        val s = "Hello World".toItemRange()
        val items = list.toItemRange()
        s.itemList
        s.previous
        println("-----")
        println(list)
        println(list.randomN(5))
        println(list)
        println(list.randomNRemove(2))
        println(list)
        val z = itemRangeOf(1, 2, 3).toMutableItemRange()
        val c = listOf(1, 2, 3).toMutableItemRange()
        val n = items.toNumberRange()
    }

    @Test
    fun sizedListStuffTest() {
        println(sizedListOf(5) { Random.nextString(5) })
        val map = listOf(1, 2, 3).toMap { it to 3 }
        println(map)
    }

    @Test
    fun stringForTimeTest() {
        //Long
        println(360000L.stringForTime())
        assertEquals("360000L should be 06:00", "06:00", 360000L.stringForTime())
        //Int
        println(360000.stringForTime())
        assertEquals("360000 should be 06:00", "06:00", 360000.stringForTime())
    }

    @Test
    fun numberTest() {
        println(30.randomString())
        println(30.randomString().length)
        println(Random.nextString(30))
        println(Random.nextString(30).length)
        println(0x0000FF.toHexString())
        println(0x0000FF.toARGB())
        println(0x0000FF.toRGB())
        println(0x0000FF.toRGB().toInt())
        println(0x0000FF)
        println(0x0000FF.toARGB().toInt())
        println(0x0000FF.toCMYK().toInt())
        println(0x0000FF.toCMYK())
        println(0x0000FF.toARGB().toRGB())
        println(0x0000FF.toRGB().toARGB())
        println(0x0000FF.toCMYK().toRGB())
        println(0x0000FF.toARGB().toCMYK())
        println(0x0000FF.toRGB().toCMYK())
        println(0x0000FF.toCMYK().toARGB())
    }

    @After
    fun finished() {
        Runtime.getRuntime().exec("say finished").waitFor()
    }

    class MultipleIterables<T, R, Y>(
        private val list: MutableList<T> = mutableListOf(),
        private val map: MutableMap<R, Y> = mutableMapOf()
    ) : MutableList<T> by list, MutableMap<R, Y> by map {
        override fun isEmpty(): Boolean = list.isEmpty() && map.isEmpty()
        override val size: Int get() = list.size + map.size
        override fun clear() {
            list.clear()
            map.clear()
        }
    }

    @Test
    fun iterableTesting() {
        val m = MultipleIterables<String, Int, String>()
        m.add("4")
        m.addAll(sizedListOf(20) { it.toString() })
        m.put(5, "4")
        m.putAll(sizedMapOf(20) { it to it.toString() })
        println(m)
        println(m.size)
        m.forEach { s: String -> println(s) }
        m.forEach { t, u -> println("$t=$u") }

        sizedSetOf(20) { it }.also(::println)

        val l = listOf(1, 2, 3, 4, 5).foldEverything(Int::toInt) { acc: Int, i: Int -> acc + i }
        println(l)
        val f = FixedMap<Int, Int>(5, FixedListLocation.END, 5)
        val f1 = FixedMap<Int, Int>(5, FixedListLocation.END, 5, 3f)
        val f2 = FixedMap<Int, Int>(5, FixedListLocation.END, 5, 3f, true)
        val s = FixedSet<Int>(5, FixedListLocation.END)
        val s1 = FixedSet<Int>(5, FixedListLocation.END, 5)
        val s2 = FixedSet<Int>(5, FixedListLocation.END, 5, 3f)
    }

    @Test
    fun rangeTest() {
        println("Item Loop = true --------")
        var f: Range<Int> = ItemRange(1, 2, 3, 4, 5)
        for (i in 0..10) {
            println(f())
            f++
        }
        println(f.previousItem)
        println(f.nextItem)
        println("-----")
        for (i in 0..10) {
            println(f())
            f--
        }
        println("Number Loop = true --------")
        var n = NumberRange(1..5)
        for (i in 0..10) {
            println(n())
            n++
        }
        println("-----")
        for (i in 0..10) {
            println(n())
            n--
        }
        println("Item Loop = false--------")
        var f1: Range<Int> = ItemRange(1, 2, 3, 4, 5)
        f1.loop = false
        for (i in 0..10) {
            println(f1())
            f1++
        }
        println("-----")
        for (i in 0..10) {
            println(f1())
            f1--
        }
        println("Number Loop = false--------")
        var n1 = NumberRange(1..5)
        n1.loop = false
        for (i in 0..10) {
            println(n1())
            n1++
        }
        println("-----")
        for (i in 0..10) {
            println(n1())
            n1--
        }

        val mut = mutableItemRangeOf(1, 2, 3, 4, 5)
        val item = itemRangeOf(1, 2, 3, 4, 5)

        println(mut)
        println(item)
        println(n)
        println(n1)
        println(f)
        println(f1)

        for (i in item) {
            println(i)
        }

        item.forEach { println(it) }

        val group = item.groupBy { it % 2 == 0 }
        println(group)

        val group2 = item.groupingBy { it % 2 == 0 }
        println(group2)

        val group3 = item.groupByCondition({ it }) { o, l -> o % 2 == l % 2 }
        println(group3)

        val group4 = item.asSequence().groupByCondition({ it }) { o, l -> o % 2 == l % 2 }
            .map { it.first to it.second.toList() }.toMap()

        //.map { it.key to it.value.toList() }.toMap()
        println(group4)
    }

    @ExperimentalTime
    @Test
    fun sequenceVsList() {

        fun sequenceTry() {
            val words = "The quick brown fox jumps over the lazy dog".split(" ")
            //convert the List to a Sequence
            val wordsSequence = words.asSequence()

            val lengthsSequence = wordsSequence.filter { println("filter: $it"); it.length > 3 }
                .map { println("length: ${it.length}"); it.length }
                .take(4)

            println("Lengths of first 4 words longer than 3 chars")
            // terminal operation: obtaining the result as a List
            println(lengthsSequence.toList())
        }

        fun listTry() {
            val words = "The quick brown fox jumps over the lazy dog".split(" ")
            val lengthsList = words.filter { println("filter: $it"); it.length > 3 }
                .map { println("length: ${it.length}"); it.length }
                .take(4)

            println("Lengths of first 4 words longer than 3 chars:")
            println(lengthsList)
        }

        val s = measureTimeMillis { sequenceTry() }
        val l = measureTimeMillis { listTry() }

        val s1 = measureNanoTime { sequenceTry() }
        val l1 = measureNanoTime { listTry() }

        val s2 = measureTime { sequenceTry() }
        val l2 = measureTime { listTry() }

        println(s)
        println(l)
        println(s1)
        println(l1)
        println(s2.inMilliseconds)
        println(l2.inMilliseconds)
        println(s2.inNanoseconds)
        println(l2.inNanoseconds)

    }

    @Test
    fun fixedLengthTest() {
        val list = FixedList<Int>(10)
        list.addAll(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11)
        println(list)
        list += 12
        println(list)
        list.add(0, 13)
        println(list)
        list.fixedSize = 5
        println(list)
        val list1 = FixedList<Int>(1, initialCapacity = 2)
        val list2 = FixedList(1, c = mutableListOf(1, 2, 3))

        val list3 = FixedList<Int>(10)
        list3.removeFrom = FixedListLocation.START
        list3.addAll(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11)
        println(list3)
        list3 += 12
        println(list3)
        list.add(0, 13)
        println(list3)
        list3.fixedSize = 5
        println(list3)

        val list4 = fixedListOf<Int>(10, 1, 2, 3, 4, 5)

        listOf(1, 2, 3).toFixedList(5).toFixedSet(10)
        fixedSetOf(5, 1, 2, 3)
        fixedMapOf(5, 1 to 4, 2 to 5)
        mapOf(1 to 3).toFixedMap(10)
    }

    @ExperimentalTime
    @Test
    fun timeTest2() {
        val format = SimpleDateFormat("MM/dd/yyyy hh:mm:ss a")

        fun formatPrint(time: Long) {
            println("$time = ${time.stringForTime()} = ${format.format(System.currentTimeMillis() + time)}")
        }

        formatPrint(0L)
        formatPrint(timeToNextHourOrHalf())
        formatPrint(nextTimeInMs(1.hours.toLongMilliseconds()) + 1.hours.toLongMilliseconds())
        formatPrint(nextTimeInMs(2.hours.toLongMilliseconds()))
        formatPrint(nextTimeInMs(30.minutes.toLongMilliseconds()))
        formatPrint(nextTimeInMs(30.minutes.toLongMilliseconds()))
        println()
        formatPrint(nextTime(30.minutes.toLongMilliseconds(), System.currentTimeMillis()))
        println(format.format(nextTime(30.minutes.toLongMilliseconds(), System.currentTimeMillis())))
        println(nextTimeInMs(30.minutes.toLongMilliseconds(), System.currentTimeMillis()))
        println(nextTime(30.minutes.toLongMilliseconds(), System.currentTimeMillis()) - System.currentTimeMillis())
        val f = Random.nextInt(1, 100)
        println(f)
        println(f % 10)
        println(f - (f % 10) + 10)

        val f1 = System.currentTimeMillis()
        println(f1)
        println(f1 % 60.minutes.toLongMilliseconds())
        println(f1 - (f1 % 60.minutes.toLongMilliseconds()) + 60.minutes.toLongMilliseconds())
        println(format.format(f1 - (f1 % 60.minutes.toLongMilliseconds()) + 60.minutes.toLongMilliseconds()))

        formatPrint(nextTimeInMs(1.hours.toLongMilliseconds()))
        formatPrint(nextTimeInMs(-1.hours.toLongMilliseconds()))
    }

    @ExperimentalTime
    @Test
    fun quickTimeTest() {
        val format = SimpleDateFormat("MM/dd/yyyy hh:mm:ss a")
        val f = nextTime(30.minutes.inMilliseconds.toLong())
        println(format.format(f))
        println(format(f))
        println(format(nextTime(1.5.hours.inMilliseconds.toLong())))
        println(format(nextTime(12.hours.inMilliseconds.toLong())))
        println(1.days.inMilliseconds.toLong())
        println(nextTimeInMs(1.days.inMilliseconds.toLong()))
        println(format(nextTime(1.days.inMilliseconds.toLong())))
        println(format(nextTime(3.days.inMilliseconds.toLong())))

        println()

        val now = System.currentTimeMillis()

        println(now)

        println(now % 1.years.inMilliseconds.toLong())
        println(30.years.inMilliseconds.toLong())

        val h = HelpfulTime()
            .add(2020, HelpfulUnit.YEARS)
            .setMonth(6)
            .add(12, HelpfulUnit.DAYS)
            .minus(5, HelpfulUnit.HOURS)

        println(format(h.time))
        println(h.time)
    }

    class HelpfulTime {
        val time get() = actions.foldRight(-1970.years.inMilliseconds.toLong()) { acc, t -> acc(t) }
        private val actions = mutableListOf<(Long) -> Long>()
        private fun MutableList<(Long) -> Long>.addToActions(time: Long) = this@HelpfulTime.apply { this@addToActions.add { it + time } }

        fun add(timeInMs: Long) = actions.addToActions(timeInMs)
        fun add(duration: HelpfulDuration<*>) = actions.addToActions(duration.inMilliseconds.toLong())
        fun add(time: Long, unit: HelpfulUnit) = actions.addToActions(HelpfulDuration(time, unit).inMilliseconds.toLong())

        fun minus(timeInMs: Long) = actions.addToActions(-timeInMs)
        fun minus(duration: HelpfulDuration<*>) = actions.addToActions(-duration.inMilliseconds.toLong())
        fun minus(time: Long, unit: HelpfulUnit) = actions.addToActions(-HelpfulDuration(time, unit).inMilliseconds.toLong())

        fun setMonth(monthCode: Int) = apply {
            val days = (0..monthCode).map { Month.of(monthCode).length(false) }.sum()
            add(days.toLong(), HelpfulUnit.DAYS)
        }

        operator fun plusAssign(timeInMs: Long) {
            add(timeInMs)
        }

        operator fun plusAssign(duration: HelpfulDuration<*>) {
            add(duration)
        }

        operator fun minusAssign(timeInMs: Long) {
            minus(timeInMs)
        }

        operator fun minusAssign(duration: HelpfulDuration<*>) {
            minus(duration)
        }
    }

    data class Numbers(val f: Int)

    object NumberChecker : SingletonObject<Numbers>() {
        override fun create(kv: Map<String, Any>): Numbers = Numbers(kv["value"] as Int)
    }

    @Test
    fun singletonTest() {
        val f = NumberChecker.getInstance("value" to 5)
        println(f)
        val f1 = NumberChecker.getInstance()
        println(f1)
        val f2 = NumberChecker.getInstance("value" to 6)
        println(f2)
    }

    @Test
    fun singletonTestTwo() {
        NumberChecker.getInstance("value" to 15)
        singletonTester()
        singletonTester2()
    }

    private fun singletonTester() {
        println(NumberChecker.getInstance())
    }

    private fun singletonTester2() {
        println(NumberChecker.getInstance())
    }

    @ExperimentalTime
    @Test
    fun timeTest() {
        val f = timeToNextHourOrHalf()
        println(f)
        val f2 = timeToNextHourOrHalf2()
        println(f2)
        println(15.minutes.inMilliseconds)
        println(30.minutes.inMilliseconds)
    }

    private fun timeToNextHourOrHalf(): Long {
        val start = ZonedDateTime.now()
        // Hour + 1, set Minute and Second to 00
        val hour = start.plusHours(1).truncatedTo(ChronoUnit.HOURS)
        val minute = start.plusHours(0).truncatedTo(ChronoUnit.HOURS)
            .plusMinutes(30).truncatedTo(ChronoUnit.MINUTES).plusSeconds(1)

        // Get Duration
        val durationHour = Duration.between(start, hour).toMillis()
        val durationMinute = Duration.between(start, minute).toMillis()
        return if (durationHour <= durationMinute) durationHour else durationMinute
    }

    private fun timeToNextHourOrHalf2(): Long {
        val half = 1_800_000
        return Date(half * ((System.currentTimeMillis() + (half / 2)/*450_000*/) / half)).time - System.currentTimeMillis()
    }

    private fun timeToNextHourOrHalf3(time: Long, timeUnit: TemporalUnit): Long {
        val half = 1_800_000
        return Date(half * ((System.currentTimeMillis() + (half / 2)/*450_000*/) / half)).time - System.currentTimeMillis()
    }

    @Test
    fun numberTesting() {
        val x: Number = 1.4
        println(x)
        println(x.toDouble())
    }

    enum class DayOfWeek { SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY }

    private fun dayOfWeek(d: Int, m: Int, y: Int): DayOfWeek {
        var y = y
        val t = intArrayOf(0, 3, 2, 5, 0, 3, 5, 1, 4, 6, 2, 4)
        y -= if (m < 3) 1 else 0
        return DayOfWeek.values()[(y + y / 4 - y / 100 + y / 400 + t[m - 1] + d) % 7]
    }

    @ExperimentalTime
    @Test
    fun durationConversion() {
        var f1 = System.currentTimeMillis()
        println(f1.toDate())
        f1 -= 1.years
        println(f1.toDate())
        f1 += 1.years
        f1 += 1.days
        println(f1.toDate())
        val f3 = System.currentTimeMillis() - 24.years - 31.days - 30.days - 31.days - 30.days - 31.days - 29.days - 31.days + 14.hours
        println(f3.toDate())

        val f = 4.years.toDuration()
        println(f.inMilliseconds.toLong())
        println(HelpfulDuration(4, HelpfulUnit.YEARS).inMilliseconds.toLong())
        val f2 = f.toHelpfulDuration()
        println(f2)
        println(3.minutes.toHelpfulDuration())
        println(HelpfulDuration(3, HelpfulUnit.MINUTES).inNanoseconds)

        println(dayOfWeek(31, 12, 1995))
        println(dayOfWeek(4, 4, 1996))
        println(dayOfWeek(31, 12, 2020))
        println(dayOfWeek(4, 4, 2020))
        val jB = Date().apply { time = 0 }.time + 1995.years
        println(jB.toDate())
        println(24.years.inMilliseconds.toLong())
        println(1995.years.inMilliseconds.toLong())
        println(1995.years.inMilliseconds.toLong() - Instant.EPOCH.epochSecond)
        println(Instant.EPOCH.epochSecond)
        println(Instant.MIN.epochSecond + 1995.years)
        println(System.currentTimeMillis() - 24.years)
        println(System.currentTimeMillis())
        println(Instant.EPOCH)
        println(1995.years.toDate())

        val jb2 = 1996.years - HelpfulDuration(1, HelpfulUnit.DAYS)

        println(jb2.inMilliseconds.toLong().toDate())
        println(jb2.toDate())
        println(System.currentTimeMillis().milliseconds.toDate())
        /*println(System.currentTimeMillis().milliseconds.toDate().toHelpfulDuration())
        println(System.currentTimeMillis().milliseconds.toDate().toHelpfulDuration().inYears.toLong())*/
    }

    private fun <T : Number> HelpfulDuration<T>.toDate(): Date {
        val year = inMilliseconds

        return Date(Date(year.toLong()).time)// - Date().time)
    }

    @ExperimentalTime
    @Test
    fun dateTest() {
        val now = System.currentTimeMillis()
        val aWeekAgo = (now - 8.days).toDate()
        val dates = mapOf(
            "an hour ago" to now - 1.hours,
            "2 hours ago" to now - 2.hours,
            "7 days ago" to now - 1.weeks,
            "9 days ago" to now - 9.days,
            "15 days ago" to now - 15.days
        )

        println(now.toDate())
        println(aWeekAgo)
        println(dates.entries.joinToString("\n") { "$it | ${it.value.toDate()} | ${it.value.isDateBetween(aWeekAgo, now.toDate())}" })
    }

    @Test
    fun operatorHelpfulDuration() {
        val f = HelpfulDuration(5, HelpfulUnit.DAYS)
        val g = HelpfulDuration(5, HelpfulUnit.HOURS)
        println(f)
        println(g)
        println(f.inHours)
        println(g.inDays)
        println("-".repeat(50))
        val p = f + g
        println(p)
        val p1 = g + f
        println(p1)
        println(p1.inDays)
        val m = f - g
        println(m)
        val m1 = g - f
        println(m1)
        println(m1.inDays)
        println("-".repeat(50))
        println(f[HelpfulUnit.HOURS])
        var f1 = HelpfulDuration(5, HelpfulUnit.DAYS)
        println(f1)
        f1++
        println(f1)

        val y = 1.years
        val d = HelpfulDuration(365, HelpfulUnit.DAYS)

        println(y)
        println(d)
        println(y.inDays)
        println(d.inYears)
        println(y == d)

        println(f > g)
        println(f < g)

        val y1 = 1.years + 1
        println(y1)
        println(1.years.inWeeks)
        println("-".repeat(50))
        val rangeInt = HelpfulDuration(30, HelpfulUnit.MINUTES)..HelpfulDuration(1, HelpfulUnit.HOURS)
        println(rangeInt)
        val rangeInt2 = HelpfulDuration(1, HelpfulUnit.HOURS)..HelpfulDuration(30, HelpfulUnit.MINUTES)
        println(rangeInt2)
        for (i in rangeInt) println(i)
        for (i in rangeInt2) println(i)
        println("-".repeat(50))
    }

    @ExperimentalTime
    @Test
    fun timingTest() {
        val kotlin = measureTimeMillis { println(1.days.inNanoseconds) }

        println("Kotlin: $kotlin")

        val helpful = measureTimeMillis { println(HelpfulUnit.DAYS.convert(1, HelpfulUnit.NANOSECONDS)) }

        println("Helpful: $helpful")

        val manual = measureTimeMillis { println(1L * 24 * 60 * 60 * 1000 * 1000 * 1000) }

        println("Manual: $manual")
    }

    @ExperimentalTime
    @Test
    fun unitTest() {

        val each = 1
            .years
            .inDays
            .toHelpfulDuration(HelpfulUnit.DAYS)
            .inHours
            .toHelpfulDuration(HelpfulUnit.HOURS)
            .inMinutes
            .toHelpfulDuration(HelpfulUnit.MINUTES)
            .inSeconds
            .seconds
            .inMilliseconds
            .milliseconds
            .inMicroseconds
            .microseconds
            .inNanoseconds
            .nanoseconds
            .inPicoseconds
            .picoseconds
            .inFemptoseconds
            .femptoseconds
            .inAttoseconds
            .attoseconds
            .inZeptoseconds
            .zeptoseconds
            .inYoctoseconds

        println(each)

        val straight = 1.years.inYoctoseconds

        println(straight)

        if (each == straight) println("$each == $straight")

        println("Top to bottom = ${1.millenniums.inYoctoseconds.toLong()}")

        println(2.weeks.inDays)
        println(2.weeks.inWeeks)
        println(1.years.inWeeks)
        println(1.decades.inYears)
        println(1.decades.inDecades)
        println(1.centuries.inCenturies)
        println(1.centuries.inYears)
        println(1.millenniums.inMillenniums)
        println(1.millenniums.inYears)

        println(HelpfulUnit.convertTo(5, HelpfulUnit.HOURS, HelpfulUnit.MINUTES))
        println(HelpfulDuration(5, HelpfulUnit.HOURS).inMinutes)

        println(HelpfulUnit.DAYS.toHelpfulString())
        println(Date(System.currentTimeMillis()).timeToNextHourOrHalf())
        println(timeToNextHour())

        println(2.years.inHours)
        println(730.days.inHours)

        val unit = HelpfulUnit.MINUTES
        println(unit.toDuration(5))
        val sevenMs = unit.convert(7.0, HelpfulUnit.MILLISECONDS)
        println(sevenMs)
        val sevenMs3 = 7.minutes.inMilliseconds
        println(sevenMs3)
        val twoHours = 2.hours.inMilliseconds
        println(twoHours)
        val twoHoursUnit = HelpfulUnit.MILLISECONDS.convert(7200000.0, HelpfulUnit.HOURS)
        println(twoHoursUnit)
        println(3.days.inMinutes)
        val threeDays = 3.toHelpfulDuration(HelpfulUnit.DAYS)
        println(threeDays.toUnit(HelpfulUnit.MINUTES))

        val oneDay = 1.toHelpfulDuration(HelpfulUnit.DAYS)
        println(oneDay)
        println(oneDay.inYears)
        println(oneDay.inDays)
        println(oneDay.inHours)
        println(oneDay.inMinutes)
        println(oneDay.inSeconds)
        println(oneDay.inMilliseconds)
        println(oneDay.inMicroseconds)
        println(oneDay.inNanoseconds)
        val oneDayInNs = oneDay.inNanoseconds.toHelpfulDuration(HelpfulUnit.NANOSECONDS)
        println(oneDayInNs)
        println(oneDayInNs.inNanoseconds)
        println(oneDayInNs.inMicroseconds)
        println(oneDayInNs.inMilliseconds)
        println(oneDayInNs.inSeconds)
        println(oneDayInNs.inMinutes)
        println(oneDayInNs.inHours)
        println(oneDayInNs.inDays)
        println(oneDayInNs.inYears)
        helpfulDurationInfo(1.seconds)
        println("-".repeat(50))
        println(HelpfulUnit.YEARS.convert(10, HelpfulUnit.DAYS, true))
    }

    private fun <T : Number> helpfulDurationInfo(unit: HelpfulDuration<T>) {
        println(unit)
        println(unit.inYears)
        println(unit.inDays)
        println(unit.inHours)
        println(unit.inMinutes)
        println(unit.inSeconds)
        println(unit.inMilliseconds)
        println(unit.inMicroseconds)
        println(unit.inNanoseconds)
        println(unit.inPicoseconds)
        println(unit.inFemptoseconds)
        println(unit.inAttoseconds)
        println(unit.inZeptoseconds)
        println(unit.inYoctoseconds)
    }

    open class Read {
        protected open val value: String = "Hello..."
        open fun append(string: String) = Unit
        override fun toString(): String = value
    }

    class WriteProtected : Read() {
        override var value: String = super.value
            private set

        override fun append(string: String) {
            value = string
        }
    }

    @Test
    fun overridenVal() {
        val f: Read = WriteProtected()
        println(f)
        f.append("asdf")
        f.append("World")
        println(f)
    }

}
