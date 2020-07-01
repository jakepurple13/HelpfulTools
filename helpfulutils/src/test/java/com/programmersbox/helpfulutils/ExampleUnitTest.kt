package com.programmersbox.helpfulutils

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Duration
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalUnit
import java.util.*
import kotlin.random.Random
import kotlin.system.measureTimeMillis
import kotlin.time.ExperimentalTime
import kotlin.time.days
import kotlin.time.hours
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

    @ExperimentalTime
    @Test
    fun dateTest() {
        val aWeekAgo = Date(System.currentTimeMillis()).apply { this.time -= 8.days.inMilliseconds.toInt() }
        //val now = isDateBetween(aWeekAgo, Date(System.currentTimeMillis()))
        val dates = mapOf(
            "an hour ago" to 1593429272678,
            "2 hours ago" to 1593429272677,
            "7 days ago" to 1592831672677,
            "9 days ago" to 1592658872677,
            "15 days ago" to 1592140472676
        )

        println(dates.entries.joinToString("\n") { "$it | ${it.value.isDateBetween(aWeekAgo, Date(System.currentTimeMillis()))}" })

    }

    /*
     an hour ago | 1593429272678
 uploaded=2 hours ago, sources=MANGA_PARK) | 1593429272677
uploaded=7 days ago, sources=MANGA_PARK) | 1592831672677
uploaded=9 days ago, sources=MANGA_PARK) | 1592658872677
uploaded=15 days ago, sources=MANGA_PARK) | 1592140472676
     */

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

        println(1.years.inWeeks)
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

}
