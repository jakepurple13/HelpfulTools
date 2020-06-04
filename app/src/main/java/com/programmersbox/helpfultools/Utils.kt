package com.programmersbox.helpfultools

import android.content.Context
import com.programmersbox.dslannotations.DslClass
import com.programmersbox.dslannotations.DslField
import com.programmersbox.gsonutils.fromJson
import com.programmersbox.helpfulutils.randomRemove
import com.programmersbox.helpfulutils.sharedPrefDelegate
import org.intellij.lang.annotations.Language

fun getRandomName() = try {
    Names.names.randomRemove()
} catch (e: IndexOutOfBoundsException) {
    "Hello"
}

var Context.randomNumber: Int? by sharedPrefDelegate()

@DslMarker
annotation class DslTestMarker

@DslMarker
annotation class DslTest2Marker

@DslClass(dslMarker = DslTestMarker::class)
class NewDsl<T, R> {
    @DslField("itemNumber")
    var numberItem = 4

    @DslField(name = "thingToTest", dslMarker = DslTest2Marker::class, comment = "This is a comment")
    var testThing: () -> Unit = {}
    var runAction: () -> Unit = {}
    var paramOne: (Int, String) -> Unit = { _, _ -> }
    var paramTwo: (Int) -> Unit = {}
    var paramThree: (Int) -> String = { "$it" }
    var paramFour = fun(_: Int) = Unit
    var paramFive = fun(_: T, _: R) = Unit
    var paramSix = fun(_: T) = Unit
    var tItem: T? = null
    var rItem: R? = null

    fun build() {
        testThing()
        runAction()
        paramOne(numberItem, paramThree(numberItem))
        paramTwo(numberItem)
        paramFour(numberItem)
        rItem?.let { tItem?.let { it1 -> paramFive(it1, it) } }
        tItem?.let { paramSix(it) }
    }

    companion object {
        fun <T, R> buildDsl(block: NewDsl<T, R>.() -> Unit) = NewDsl<T, R>().apply(block).build()
    }
}

@Language("JSON")
val colorApiBlack = """
    {
          "XYZ": {
            "X": 0,
            "Y": 0,
            "Z": 0,
            "fraction": {},
            "value": "XYZ(0, 0, 0)"
          },
          "_embedded": {},
          "_links": {
            "self": {
              "href": "/id?hex\u003d000000"
            }
          },
          "cmyk": {
            "fraction": {
              "k": 1
            },
            "k": 100,
            "value": "cmyk(NaN, NaN, NaN, 100)"
          },
          "contrast": {
            "value": "#ffffff"
          },
          "hex": {
            "clean": "000000",
            "value": "#000000"
          },
          "hsl": {
            "fraction": {},
            "h": 0,
            "l": 0,
            "s": 0,
            "value": "hsl(0, 0%, 0%)"
          },
          "hsv": {
            "fraction": {},
            "h": 0,
            "s": 0,
            "v": 0,
            "value": "hsv(0, 0%, 0%)"
          },
          "image": {
            "bare": "http://www.thecolorapi.com/id?format\u003dsvg\u0026named\u003dfalse\u0026hex\u003d000000",
            "named": "http://www.thecolorapi.com/id?format\u003dsvg\u0026hex\u003d000000"
          },
          "name": {
            "closest_named_hex": "#000000",
            "distance": 0,
            "exact_match_name": true,
            "value": "Black"
          },
          "rgb": {
            "b": 0,
            "fraction": {},
            "g": 0,
            "r": 0,
            "value": "rgb(0, 0, 0)"
          }
        }

""".trimIndent().fromJson<ColorApi>()!!