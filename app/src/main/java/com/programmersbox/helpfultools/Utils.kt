package com.programmersbox.helpfultools

import com.programmersbox.gsonutils.fromJson
import org.intellij.lang.annotations.Language

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