package com.programmersbox.testingplaygroundapp

import com.programmersbox.thirdpartyutils.*
import io.reactivex.Observable
import org.junit.Test
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


class ExampleUnitTestThree {

    @Test
    fun retrofitTest() {

        println(10_000 + 8_500 + 7_500 - 5_163)

        val s = "http://thecolorapi.com/id?hex=000000"

        val retrofit = gsonFit("http://thecolorapi.com/")

        val c = retrofit.create(ColorInterface::class.java)

        val h = c.getColorFromHex("6857F3").execute().body()

        println(h)

        val x = "http://xkcd.com/614/info.0.json"

        val fit2 = gsonFit("http://xkcd.com/").create<XkcdService>()
        println(fit2.getPage(6).execute().body())
        println(fit2.getCurrent().execute().body())
        fit2.getPageRx(6).subscribe { println(it) }
        fit2.getCurrentRx().subscribe { println(it) }

        val c1 = c.getColorFromHex("6789876543").executeForResult()
        println(c1)
        when (c1) {
            is Result.Success -> println(c1.response.body())
            is Result.Failure -> println(c1.error.message)
        }
        c.getColorFromHex("6789876543").enqueue { println(it) }

        c.getColorFromHex("123456").enqueueDsl {
            onResponse { println(it.body()) }
            onFailure { println(it) }
        }

    }

    private fun gsonFit(url: String, builder: Retrofit.Builder.() -> Unit = {}) = Retrofit.Builder()
        .baseUrl(url)
        .apply(builder)
        .gsonConverter()
        .rx2FactorySync()
        .build()

}

interface XkcdService {
    @GET("{id}/info.0.json")
    fun getPage(@Path("id") comicNumber: Int): Call<XKCD>

    @GET("info.0.json")
    fun getCurrent(): Call<XKCD>

    @GET("{id}/info.0.json")
    fun getPageRx(@Path("id") comicNumber: Int): Observable<XKCD>

    @GET("info.0.json")
    fun getCurrentRx(): Observable<XKCD>
}

data class XKCD(
    val month: String?,
    val num: Number?,
    val link: String?,
    val year: String?,
    val news: String?,
    val safe_title: String?,
    val transcript: String?,
    val alt: String?,
    val img: String?,
    val title: String?,
    val day: String?
)


interface ColorInterface {
    @GET("id?")
    fun getColorFromHex(@Query("hex") hex: String): Call<ColorApi>
}

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