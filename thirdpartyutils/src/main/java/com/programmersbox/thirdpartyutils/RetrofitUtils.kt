package com.programmersbox.thirdpartyutils

import io.reactivex.Scheduler
import retrofit.converter.guava.GuavaOptionalConverterFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.guava.GuavaCallAdapterFactory
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.jackson.JacksonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.protobuf.ProtoConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.converter.wire.WireConverterFactory

fun Retrofit.Builder.gsonConverter(): Retrofit.Builder = addConverterFactory(GsonConverterFactory.create())
fun Retrofit.Builder.moshiConverter(): Retrofit.Builder = addConverterFactory(MoshiConverterFactory.create())
fun Retrofit.Builder.wireConverter(): Retrofit.Builder = addConverterFactory(WireConverterFactory.create())
fun Retrofit.Builder.scalarsConverter(): Retrofit.Builder = addConverterFactory(ScalarsConverterFactory.create())
fun Retrofit.Builder.protobufConverter(): Retrofit.Builder = addConverterFactory(ProtoConverterFactory.create())
fun Retrofit.Builder.jacksonConverter(): Retrofit.Builder = addConverterFactory(JacksonConverterFactory.create())
fun Retrofit.Builder.guavaConverter(): Retrofit.Builder = addConverterFactory(GuavaOptionalConverterFactory.create())

fun Retrofit.Builder.rx2FactorySync(): Retrofit.Builder = addCallAdapterFactory(RxJava2CallAdapterFactory.create())
fun Retrofit.Builder.rx2FactoryAsync(): Retrofit.Builder = addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())
fun Retrofit.Builder.rx2FactoryScheduler(scheduler: Scheduler): Retrofit.Builder =
    addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(scheduler))

fun Retrofit.Builder.guavaFactory(): Retrofit.Builder = addCallAdapterFactory(GuavaCallAdapterFactory.create())

sealed class Result<T> {
    data class Success<T>(val call: Call<T>, val response: Response<T>) : Result<T>()
    data class Failure<T>(val call: Call<T>, val error: Throwable) : Result<T>()
}

inline fun <reified T> Call<T>.enqueue(crossinline result: (Result<T>) -> Unit) {
    enqueue(object : Callback<T> {
        override fun onFailure(call: Call<T>, error: Throwable) = result(Result.Failure(call, error))
        override fun onResponse(call: Call<T>, response: Response<T>) = result(Result.Success(call, response))
    })
}

inline fun <reified T> Call<T>.executeForResult(): Result<T> = try {
    Result.Success(this, execute())
} catch (e: Exception) {
    Result.Failure(this, e)
}

fun <T> Call<T>.enqueueDsl(callback: CallBackKt<T>.() -> Unit) = enqueue(CallBackKt<T>().apply(callback))

class CallBackKt<T> : Callback<T> {

    private var onResponse: (Response<T>) -> Unit = {}
    private var onFailure: (t: Throwable?) -> Unit = {}

    fun onResponse(block: (Response<T>) -> Unit) = run { onResponse = block }
    fun onFailure(block: (t: Throwable?) -> Unit) = run { onFailure = block }

    override fun onFailure(call: Call<T>, t: Throwable) = onFailure(t)
    override fun onResponse(call: Call<T>, response: Response<T>) = onResponse(response)

}