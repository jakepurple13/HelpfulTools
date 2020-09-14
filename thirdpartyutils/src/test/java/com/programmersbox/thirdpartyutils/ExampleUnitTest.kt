package com.programmersbox.thirdpartyutils

import io.reactivex.Single
import okhttp3.OkHttpClient
import org.junit.Assert.assertEquals
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun retroFitTest() {
        Agify.buildService().getList("Jacob", "Jordan", "Bako").subscribe { it, t2 -> println(it) }

        Genderize.buildService().getList("Jacob", "Jordan", "Bako").subscribe { it, t2 -> println(it) }

        Nationalize.buildService().getList("Jacob", "Jordan", "Bako").subscribe { it, t2 -> println(it) }
    }

    class Ify {
        private val agify by lazy { Agify.buildService() }
        private val genderize by lazy { Genderize.buildService() }
        private val nationalize by lazy { Nationalize.buildService() }

        //fun getIfyInfo(vararg name: String) = Single

    }

    object Agify {
        private const val baseUrl = "https://api.agify.io"

        private val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .build()
                chain.proceed(request)
            }
            .build()

        fun buildService(): AgifyService = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .gsonConverter()
            .rx2FactorySync()
            .build()
            .create()

        interface AgifyService {

            data class AgifyInfo(val name: String, val age: Int, val count: Int)

            @GET("?")
            fun getList(@Query("name[]") vararg name: String): Single<List<AgifyInfo>>

        }
    }

    object Genderize {
        private const val baseUrl = "https://api.genderize.io/"

        private val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .build()
                chain.proceed(request)
            }
            .build()

        fun buildService(): GenderizeService = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .gsonConverter()
            .rx2FactorySync()
            .build()
            .create()

        interface GenderizeService {

            data class GenderizeInfo(val name: String, val gender: String, val probability: Float, val count: Int)

            @GET("?")
            fun getList(@Query("name[]") vararg name: String): Single<List<GenderizeInfo>>

        }
    }

    object Nationalize {
        private const val baseUrl = "https://api.nationalize.io"

        private val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .build()
                chain.proceed(request)
            }
            .build()

        fun buildService(): NationalizeService = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .gsonConverter()
            .rx2FactorySync()
            .build()
            .create()

        interface NationalizeService {

            data class Country(val country_id: String, val probability: Float)

            data class NationalizeInfo(val name: String, val country: List<Country>)

            @GET("?")
            fun getList(@Query("name[]") vararg name: String): Single<List<NationalizeInfo>>

        }
    }
}
