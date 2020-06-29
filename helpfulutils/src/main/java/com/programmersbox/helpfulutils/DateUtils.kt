package com.programmersbox.helpfulutils

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.Duration
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
fun timeToNextHour(): Long {
    val start = ZonedDateTime.now()
    // Hour + 1, set Minute and Second to 00
    val end = start.plusHours(1).truncatedTo(ChronoUnit.HOURS)

    // Get Duration
    val duration = Duration.between(start, end)
    return duration.toMillis()
}

@RequiresApi(Build.VERSION_CODES.O)
fun timeToNextHourOrHalf(): Long {
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

fun timeToNext(minuteInMs: Long = 1_800_000) =
    Date(minuteInMs * ((System.currentTimeMillis() + (minuteInMs / 2)) / minuteInMs)).time - System.currentTimeMillis()

val Calendar.timeToNextHourOrHalf
    @RequiresApi(Build.VERSION_CODES.O)
    get() = timeToNextHourOrHalf()

val Calendar.timeToNextHour
    @RequiresApi(Build.VERSION_CODES.O)
    get() = timeToNextHour()

fun Date.isBetween(min: Date, max: Date) = after(min) && before(max)

fun Long.isDateBetween(min: Date, max: Date) = Date(this).isBetween(min, max)

fun Date.isBetween(min: Long, max: Long) = isBetween(Date(min), Date(max))

fun Long.isDateBetween(min: Long, max: Long) = Date(this).isBetween(min, max)

val <T : Number> T.days get() = toHelpfulDuration(HelpfulUnit.DAYS)
val <T : Number> T.hours get() = toHelpfulDuration(HelpfulUnit.HOURS)
val <T : Number> T.minutes get() = toHelpfulDuration(HelpfulUnit.MINUTES)
val <T : Number> T.seconds get() = toHelpfulDuration(HelpfulUnit.SECONDS)
val <T : Number> T.milliseconds get() = toHelpfulDuration(HelpfulUnit.MILLISECONDS)
val <T : Number> T.microseconds get() = toHelpfulDuration(HelpfulUnit.MICROSECONDS)
val <T : Number> T.nanoseconds get() = toHelpfulDuration(HelpfulUnit.NANOSECONDS)

fun <T : Number> T.toHelpfulDuration(unit: HelpfulUnit) = HelpfulDuration(this, unit)

data class HelpfulDuration<T : Number>(val number: T, val unit: HelpfulUnit) {
    fun toUnit(unit: HelpfulUnit) = this.unit.convert(number.toLong(), unit)
    val inDays get() = toUnit(HelpfulUnit.DAYS)
    val inHours get() = toUnit(HelpfulUnit.HOURS)
    val inMinutes get() = toUnit(HelpfulUnit.MINUTES)
    val inSeconds get() = toUnit(HelpfulUnit.SECONDS)
    val inMilliseconds get() = toUnit(HelpfulUnit.MILLISECONDS)
    val inMicroseconds get() = toUnit(HelpfulUnit.MICROSECONDS)
    val inNanoseconds get() = toUnit(HelpfulUnit.NANOSECONDS)
}

enum class HelpfulUnit(
    private val downOne: (Long) -> Long = { it },
    private val upOne: (Long) -> Long = { it }
) {
    /**
     * Time unit representing one nanosecond, which is 1/1000 of a microsecond.
     */
    NANOSECONDS(
        upOne = { it / 1000 }
    ),

    /**
     * Time unit representing one microsecond, which is 1/1000 of a millisecond.
     */
    MICROSECONDS(
        downOne = { it * 1000 },
        upOne = { it / 1000 }
    ),

    /**
     * Time unit representing one millisecond, which is 1/1000 of a second.
     */
    MILLISECONDS(
        downOne = { it * 1000 },
        upOne = { it / 1000 }
    ),

    /**
     * Time unit representing one second.
     */
    SECONDS(
        downOne = { it * 1000 },
        upOne = { it / 60 }
    ),

    /**
     * Time unit representing one minute.
     */
    MINUTES(
        downOne = { it * 60 },
        upOne = { it / 60 }
    ),

    /**
     * Time unit representing one hour.
     */
    HOURS(
        downOne = { it * 60 },
        upOne = { it / 24 }
    ),

    /**
     * Time unit representing one day, which is always equal to 24 hours.
     */
    DAYS(
        downOne = { it * 24 }
    );

    fun convert(number: Long, unit: HelpfulUnit): Long = if (this == unit)
        number
    else {
        if (priority >= unit.priority) downUnit?.convert(downOne(number), unit) ?: downOne(number)
        else upUnit?.convert(upOne(number), unit) ?: upOne(number)
    }

    private val priority
        get() = when (this) {
            NANOSECONDS -> 0
            MICROSECONDS -> 1
            MILLISECONDS -> 2
            SECONDS -> 3
            MINUTES -> 4
            HOURS -> 5
            DAYS -> 6
        }

    private val downUnit: HelpfulUnit?
        get() = when (this) {
            NANOSECONDS -> null
            MICROSECONDS -> NANOSECONDS
            MILLISECONDS -> MICROSECONDS
            SECONDS -> MILLISECONDS
            MINUTES -> SECONDS
            HOURS -> MINUTES
            DAYS -> HOURS
        }

    private val upUnit: HelpfulUnit?
        get() = when (this) {
            NANOSECONDS -> MICROSECONDS
            MICROSECONDS -> MILLISECONDS
            MILLISECONDS -> SECONDS
            SECONDS -> MINUTES
            MINUTES -> HOURS
            HOURS -> DAYS
            DAYS -> null
        }
}
