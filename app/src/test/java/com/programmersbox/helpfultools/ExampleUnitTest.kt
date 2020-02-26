package com.programmersbox.helpfultools

import com.programmersbox.loggingutils.Loged
import com.programmersbox.loggingutils.f
import org.junit.Before
import org.junit.Test
import java.util.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    @Before
    fun setup() {
        Loged.UNIT_TESTING = true
        Loged.FILTER_BY_CLASS_NAME = "com.programmersbox.helpfultools"
    }

    @Test
    fun addition_isCorrect() {
        Loged.f(stringForTime(10000000))
    }

    fun stringForTime(milliseconds: Long): String? {
        var millisecond = milliseconds
        if (millisecond < 0 || millisecond >= 24 * 60 * 60 * 1000) return "00:00"
        millisecond /= 1000
        var minute = (millisecond / 60).toInt()
        val hour = minute / 60
        val second = (millisecond % 60).toInt()
        minute %= 60
        val stringBuilder = StringBuilder()
        val mFormatter = Formatter(stringBuilder, Locale.getDefault())
        return if (hour > 0) {
            mFormatter.format("%02d:%02d:%02d", hour, minute, second)
        } else {
            mFormatter.format("%02d:%02d", minute, second)
        }.toString()
    }
}