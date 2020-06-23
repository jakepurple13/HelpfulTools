package com.programmersbox.gsonutils

import android.content.Context
import android.content.Intent
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import com.programmersbox.helpfulutils.FixedListLocation
import com.programmersbox.helpfulutils.addAll
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.programmersbox.gsonutils.test", appContext.packageName)
    }

    data class AnotherObject(val item: String)

    data class GsonObject(var string: String, val int: Int, val anotherObject: AnotherObject)

    @Test
    fun gsonTest() {
        val item = GsonObject("Hello", 5, AnotherObject("World"))
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        Intent(appContext, ExampleInstrumentedTest::class.java).apply {
            putExtra("gson", item)
        }.apply {
            val getting = getObjectExtra<GsonObject?>("gson", null)
            println(getting)
        }

        appContext.getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE).edit().putObject("gson", item).apply()
        val getting = appContext.getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE).getObject<GsonObject>("gson")
        println(getting)
        appContext.getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE).updateObject<GsonObject>("gson") { string = "Hello" }
        val getting1 = appContext.getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE).getObject<GsonObject>("gson")
        println(getting1)
    }

    @Test
    fun jsonTesting() {
        val f = FixedListTwo<Int>(5, FixedListLocation.END)
        f.addAll(1, 2, 3, 4, 5)

        println(f.toJson2())

        //println(f.toJson2())

        val f1 = f.toJson2().fromJson2<FixedListTwo<Int>>()//JSONObject().getJSONObject(f.toJson2()).getJSONObject("nameValuePairs")
        /*.let {
            FixedListTwo(
                it.getInt("fixedSize"),
                it.getString("removeFrom").fromJson2<FixedListLocation>()!!,
                it.getString("itemList").fromJson2<MutableList<Int>>()!!
            )
        }*/

        println(f1)
    }

    inline fun <reified T> String?.fromJson2(): T? = try {
        GsonBuilder()
            .registerTypeAdapter(FixedListTwo::class.java, FixedListAdapter::class.java)
            .create()
            .fromJson(this, object : TypeToken<T>() {}.type)
    } catch (e: Exception) {
        null
    }

    fun Any?.toJson2(): String = GsonBuilder()
        .registerTypeAdapter(FixedListTwo::class.java, FixedListAdapter::class.java)
        .create()
        .toJson(this)

    class FixedListAdapter : TypeAdapter<FixedListTwo<*>>() {
        override fun write(out: JsonWriter?, value: FixedListTwo<*>?) {
            out
                ?.beginObject()
                ?.name("removeFrom")?.value(value?.removeFrom?.name)
                ?.name("fixedSize")?.value(value?.fixedSize)
                ?.beginArray()
                ?.name("list")?.value(value?.toJson())
                ?.endArray()
                ?.endObject()
        }

        override fun read(`in`: JsonReader?): FixedListTwo<*> {
            val remove = `in`?.nextString()
            val size = `in`?.nextInt()
            val list = `in`?.nextString()
            return FixedListTwo(size!!, FixedListLocation.valueOf(remove!!), list!!.fromJson<List<*>>()!!.toMutableList())
        }

    }

    data class FixedListTwo<T>(
        private var internalFixedSize: Int,
        /**
         * choose where to remove items from
         * Default is [FixedListLocation.END]
         */
        var removeFrom: FixedListLocation = FixedListLocation.END,
        private val itemList: MutableList<T> = mutableListOf()
    ) : MutableList<T> by itemList {

        /*fun toJson() = JsonObject().apply {
            addProperty("fixedSize", internalFixedSize)
            addProperty("removeFrom", removeFrom.toJson())
            addProperty("itemList", itemList.toJson())
        }.toJson()

        fun toJson2() = JSONObject().apply {
            put("fixedSize", internalFixedSize)
            put("removeFrom", removeFrom.toJson())
            put("itemList", itemList.toJson())
        }.toJson()*/

        var fixedSize: Int
            get() = internalFixedSize
            set(value) {
                require(value > 0) { "FixedSize must be greater than 0" }
                internalFixedSize = value
                multipleSizeCheck()
            }

        private val removeIndex
            get() = when (removeFrom) {
                FixedListLocation.START -> 0
                FixedListLocation.END -> lastIndex
            }

        protected fun singleSizeCheck() {
            if (size > fixedSize) removeAt(removeIndex)
        }

        override fun add(element: T): Boolean {
            val addition = itemList.add(element)
            singleSizeCheck()
            return addition
        }

        override fun add(index: Int, element: T) {
            val addition = itemList.add(index, element)
            singleSizeCheck()
            return addition
        }

        protected fun multipleSizeCheck() {
            while (size > fixedSize) removeAt(removeIndex)
        }

        override fun addAll(elements: Collection<T>): Boolean {
            val addition = itemList.addAll(elements)
            multipleSizeCheck()
            return addition
        }

        override fun addAll(index: Int, elements: Collection<T>): Boolean {
            val addition = itemList.addAll(index, elements)
            multipleSizeCheck()
            return addition
        }
    }
}