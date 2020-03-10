package com.programmersbox.helpfultools

data class ColorApi(
    val hex: Hex?,
    val rgb: Rgb?,
    val hsl: Hsl?,
    val hsv: Hsv?,
    val name: Name?,
    val cmyk: Cmyk?,
    val XYZ: XYZ?,
    val image: Image?,
    val contrast: Contrast?,
    val _links: _links?,
    val _embedded: _embedded?
)

data class Cmyk(val fraction: Fraction?, val value: String?, val c: Number?, val m: Number?, val y: Number?, val k: Number?)

data class Contrast(val value: String?)

data class Fraction(val c: Number?, val m: Number?, val y: Number?, val k: Number?)

data class Hex(val value: String?, val clean: String?)

data class Hsl(val fraction: Fraction?, val h: Number?, val s: Number?, val l: Number?, val value: String?)

data class Hsv(val fraction: Fraction?, val value: String?, val h: Number?, val s: Number?, val v: Number?)

data class Image(val bare: String?, val named: String?)

data class Name(val value: String?, val closest_named_hex: String?, val exact_match_name: Boolean?, val distance: Number?)

data class Rgb(val fraction: Fraction?, val r: Number?, val g: Number?, val b: Number?, val value: String?)

data class Self(val href: String?)

data class XYZ(val fraction: Fraction?, val value: String?, val X: Number?, val Y: Number?, val Z: Number?)

class _embedded()

data class _links(val self: Self?)