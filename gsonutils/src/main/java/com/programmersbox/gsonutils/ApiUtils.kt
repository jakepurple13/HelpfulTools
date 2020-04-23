package com.programmersbox.gsonutils

import androidx.annotation.WorkerThread
import okhttp3.OkHttpClient

@WorkerThread
fun getApi(url: String, builder: okhttp3.Request.Builder.() -> Unit = {}): String? {
    val request = okhttp3.Request.Builder()
        .url(url)
        .apply(builder)
        .get()
        .build()
    val response = OkHttpClient().newCall(request).execute()
    return if (response.code == 200) response.body!!.string() else null
}

@WorkerThread
inline fun <reified T> getJsonApi(url: String, noinline builder: okhttp3.Request.Builder.() -> Unit = {}) = getApi(url, builder).fromJson<T>()