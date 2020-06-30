package com.programmersbox.helpfulutils

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.Duration
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.util.*

/**
 * Get how much time there is until the next hour in milliseconds
 */
@RequiresApi(Build.VERSION_CODES.O)
fun timeToNextHour(): Long {
    val start = ZonedDateTime.now()
    // Hour + 1, set Minute and Second to 00
    val end = start.plusHours(1).truncatedTo(ChronoUnit.HOURS)

    // Get Duration
    val duration = Duration.between(start, end)
    return duration.toMillis()
}

/**
 * Get how much time there is until the next hour or half hour in milliseconds
 */
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

/**
 * Get how much time there is until the next [minuteInMs] in milliseconds
 * e.g. 1,800,000 = 30 minutes, so this will return how many minutes until xx:30 or xx:00
 */
@JvmOverloads
fun timeToNext(minuteInMs: Long = 1_800_000, currentTime: Long = System.currentTimeMillis()) =
    Date(minuteInMs * ((currentTime + (minuteInMs / 2)) / minuteInMs)).time - currentTime

/**
 * @see timeToNextHourOrHalf
 * @see timeToNext
 */
fun Date.timeToNextHourOrHalf(): Long = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
    dateCalTimeToNextHourOrHalf(ZonedDateTime.ofInstant(toInstant(), ZoneId.systemDefault()))
} else {
    timeToNext(currentTime = time)
}

/**
 * @see timeToNextHourOrHalf
 * @see timeToNext
 */
fun Calendar.timeToNextHourOrHalf(): Long = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
    dateCalTimeToNextHourOrHalf(ZonedDateTime.ofInstant(toInstant(), ZoneId.systemDefault()))
} else {
    timeToNext(currentTime = timeInMillis)
}

@RequiresApi(Build.VERSION_CODES.O)
private fun dateCalTimeToNextHourOrHalf(start: ZonedDateTime): Long {
    // Hour + 1, set Minute and Second to 00
    val hour = start.plusHours(1).truncatedTo(ChronoUnit.HOURS)
    val minute = start.plusHours(0).truncatedTo(ChronoUnit.HOURS)
        .plusMinutes(30).truncatedTo(ChronoUnit.MINUTES).plusSeconds(1)

    // Get Duration
    val durationHour = Duration.between(start, hour).toMillis()
    val durationMinute = Duration.between(start, minute).toMillis()
    return if (durationHour <= durationMinute) durationHour else durationMinute
}

/**
 * Check if a date is between [min] and [max]
 */
fun Date.isBetween(min: Date, max: Date) = after(min) && before(max)

/**
 * Check if a date is between [min] and [max]
 */
fun Long.isDateBetween(min: Date, max: Date) = Date(this).isBetween(min, max)

/**
 * Check if a date is between [min] and [max]
 */
fun Date.isBetween(min: Long, max: Long) = isBetween(Date(min), Date(max))

/**
 * Check if a date is between [min] and [max]
 */
fun Long.isDateBetween(min: Long, max: Long) = Date(this).isBetween(min, max)

/**
 * Converts [this] into a helpful duration
 */
fun <T : Number> T.toHelpfulDuration(unit: HelpfulUnit) = HelpfulDuration(this, unit)

/**
 * @see Number.toHelpfulDuration
 */
fun <T : Number> HelpfulUnit.toDuration(number: T) = HelpfulDuration(number, this)

//units that have an enum
val <T : Number> T.years get() = toHelpfulDuration(HelpfulUnit.YEARS)
val <T : Number> T.days get() = toHelpfulDuration(HelpfulUnit.DAYS)
val <T : Number> T.hours get() = toHelpfulDuration(HelpfulUnit.HOURS)
val <T : Number> T.minutes get() = toHelpfulDuration(HelpfulUnit.MINUTES)
val <T : Number> T.seconds get() = toHelpfulDuration(HelpfulUnit.SECONDS)
val <T : Number> T.milliseconds get() = toHelpfulDuration(HelpfulUnit.MILLISECONDS)
val <T : Number> T.microseconds get() = toHelpfulDuration(HelpfulUnit.MICROSECONDS)
val <T : Number> T.nanoseconds get() = toHelpfulDuration(HelpfulUnit.NANOSECONDS)
val <T : Number> T.picoseconds get() = toHelpfulDuration(HelpfulUnit.PICOSECONDS)
val <T : Number> T.femptoseconds get() = toHelpfulDuration(HelpfulUnit.FEMTOSECONDS)
val <T : Number> T.attoseconds get() = toHelpfulDuration(HelpfulUnit.ATTOSECONDS)
val <T : Number> T.zeptoseconds get() = toHelpfulDuration(HelpfulUnit.ZEPTOSECONDS)
val <T : Number> T.yoctoseconds get() = toHelpfulDuration(HelpfulUnit.YOCTOSECONDS)

//units that don't have an enum
val <T : Number> T.weeks get() = (toDouble() * 7).toHelpfulDuration(HelpfulUnit.DAYS)
val <T : Number> T.decades get() = (toDouble() * 10).toHelpfulDuration(HelpfulUnit.YEARS)
val <T : Number> T.centuries get() = (toDouble() * 100).toHelpfulDuration(HelpfulUnit.YEARS)
val <T : Number> T.millenniums get() = (toDouble() * 1000).toHelpfulDuration(HelpfulUnit.YEARS)

//units that don't have an enum
val <T : Number> HelpfulDuration<T>.inWeeks get() = inDays / 7
val <T : Number> HelpfulDuration<T>.inDecades get() = inYears / 10
val <T : Number> HelpfulDuration<T>.inCenturies get() = inYears / 100
val <T : Number> HelpfulDuration<T>.inMillenniums get() = inYears / 1000

data class HelpfulDuration<T : Number>(val number: T, val unit: HelpfulUnit) {
    fun toUnit(unit: HelpfulUnit) = this.unit.convert(number, unit)
    val inYears get() = toUnit(HelpfulUnit.YEARS)
    val inDays get() = toUnit(HelpfulUnit.DAYS)
    val inHours get() = toUnit(HelpfulUnit.HOURS)
    val inMinutes get() = toUnit(HelpfulUnit.MINUTES)
    val inSeconds get() = toUnit(HelpfulUnit.SECONDS)
    val inMilliseconds get() = toUnit(HelpfulUnit.MILLISECONDS)
    val inMicroseconds get() = toUnit(HelpfulUnit.MICROSECONDS)
    val inNanoseconds get() = toUnit(HelpfulUnit.NANOSECONDS)
    val inPicoseconds get() = toUnit(HelpfulUnit.PICOSECONDS)
    val inFemptoseconds get() = toUnit(HelpfulUnit.FEMTOSECONDS)
    val inAttoseconds get() = toUnit(HelpfulUnit.ATTOSECONDS)
    val inZeptoseconds get() = toUnit(HelpfulUnit.ZEPTOSECONDS)
    val inYoctoseconds get() = toUnit(HelpfulUnit.YOCTOSECONDS)
}

enum class HelpfulUnit(
    private val downOne: (Double) -> Double = { it },
    private val upOne: (Double) -> Double = { it }
) {

    /**
     * Time unit representing one yoctosecond, which is 1/1000 of a zeptosecond.
     */
    YOCTOSECONDS(
        upOne = { it / 1000 }
    ),

    /**
     * Time unit representing one zeptosecond, which is 1/1000 of a attosecond.
     */
    ZEPTOSECONDS(
        downOne = { it * 1000 },
        upOne = { it / 1000 }
    ),

    /**
     * Time unit representing one attosecond, which is 1/1000 of a femtosecond.
     */
    ATTOSECONDS(
        downOne = { it * 1000 },
        upOne = { it / 1000 }
    ),

    /**
     * Time unit representing one femtosecond, which is 1/1000 of a picosecond.
     */
    FEMTOSECONDS(
        downOne = { it * 1000 },
        upOne = { it / 1000 }
    ),

    /**
     * Time unit representing one picosecond, which is 1/1000 of a nanosecond.
     */
    PICOSECONDS(
        downOne = { it * 1000 },
        upOne = { it / 1000 }
    ),

    /**
     * Time unit representing one nanosecond, which is 1/1000 of a microsecond.
     */
    NANOSECONDS(
        downOne = { it * 1000 },
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
        downOne = { it * 24 },
        upOne = { it / 365 }
    ),

    /**
     * Time unit representing one year, which is always equal to 365 days.
     */
    YEARS(
        downOne = { it * 365 }
    ) {
        override fun <T : Number> convert(number: T, unit: HelpfulUnit, includeLeapYears: Boolean): Double {
            var original = convert(number, unit)
            if (includeLeapYears) original += number.toDouble() / 4
            return original
        }
    };

    /**
     * Convert [number] to another [unit]
     */
    fun <T : Number> convert(number: T, unit: HelpfulUnit): Double = if (this == unit)
        number.toDouble()
    else {
        if (priority >= unit.priority) downUnit?.convert(downOne(number.toDouble()), unit) ?: downOne(number.toDouble())
        else upUnit?.convert(upOne(number.toDouble()), unit) ?: upOne(number.toDouble())
    }

    /**
     * If you are dealing with [YEARS] and want to include leap years
     * @see convert
     */
    open fun <T : Number> convert(number: T, unit: HelpfulUnit, includeLeapYears: Boolean): Double = convert(number, unit)

    private val priority get() = ordinal

    private val downUnit: HelpfulUnit?
        get() = when (this) {
            YOCTOSECONDS -> null
            ZEPTOSECONDS -> YOCTOSECONDS
            ATTOSECONDS -> ZEPTOSECONDS
            FEMTOSECONDS -> ATTOSECONDS
            PICOSECONDS -> FEMTOSECONDS
            NANOSECONDS -> PICOSECONDS
            MICROSECONDS -> NANOSECONDS
            MILLISECONDS -> MICROSECONDS
            SECONDS -> MILLISECONDS
            MINUTES -> SECONDS
            HOURS -> MINUTES
            DAYS -> HOURS
            YEARS -> DAYS
        }

    private val upUnit: HelpfulUnit?
        get() = when (this) {
            YOCTOSECONDS -> ZEPTOSECONDS
            ZEPTOSECONDS -> ATTOSECONDS
            ATTOSECONDS -> FEMTOSECONDS
            FEMTOSECONDS -> PICOSECONDS
            PICOSECONDS -> NANOSECONDS
            NANOSECONDS -> MICROSECONDS
            MICROSECONDS -> MILLISECONDS
            MILLISECONDS -> SECONDS
            SECONDS -> MINUTES
            MINUTES -> HOURS
            HOURS -> DAYS
            DAYS -> YEARS
            YEARS -> null
        }

    fun toHelpfulString() = "$this(down=$downUnit, up=$upUnit)"

    companion object {
        tailrec fun <T : Number> convertTo(number: T, fromUnit: HelpfulUnit, toUnit: HelpfulUnit): Double = if (fromUnit == toUnit) number.toDouble()
        else convertTo(
            if (fromUnit.priority >= toUnit.priority) fromUnit.downOne(number.toDouble()) else fromUnit.upOne(number.toDouble()),
            if (fromUnit.priority >= toUnit.priority) fromUnit.downUnit!! else fromUnit.upUnit!!, toUnit
        )
    }
}
