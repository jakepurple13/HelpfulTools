package com.programmersbox.gsonutils

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken

/**
 * Put an object into sharedpreferences
 * This converts the object into a json string
 */
fun <T> SharedPreferences.Editor.putObject(key: String, value: T): SharedPreferences.Editor = putString(key, value.toJson())

/**
 * gets an object from sharedpreferences
 */
inline fun <reified T> SharedPreferences.getObject(key: String, defaultValue: T? = null, vararg adapters: Pair<Class<*>, Any>): T? = try {
    getString(key, null)?.fromJson<T>(*adapters) ?: defaultValue
} catch (e: JsonSyntaxException) {
    null
}

/**
 * converts [this] to a Json string
 */
fun Any?.toJson(): String = Gson().toJson(this)

/**
 * converts [this] to a Json string but its formatted nicely
 */
fun Any?.toPrettyJson(): String = GsonBuilder().setPrettyPrinting().create().toJson(this)

/**
 * Takes [this] and coverts it to an object
 */
inline fun <reified T> String?.fromJson(): T? = try {
    Gson().fromJson(this, object : TypeToken<T>() {}.type)
} catch (e: Exception) {
    null
}

/**
 * Takes [this] and coverts it to an object
 * @param adapters Pair<Class<*>, Any>
 *
 * -----------------
 *
 * First - The class to register to.
 *
 * -----------------
 *
 * Second - This object must implement at least one of
 * @see com.google.gson.TypeAdapter
 * @see com.google.gson.InstanceCreator
 * @see com.google.gson.JsonSerializer
 * @see com.google.gson.JsonDeserializer
 */
inline fun <reified T> String?.fromJson(vararg adapters: Pair<Class<*>, Any>): T? = try {
    GsonBuilder().registerTypeAdapters(*adapters).create().fromJson(this, object : TypeToken<T>() {}.type)
} catch (e: Exception) {
    null
}

fun GsonBuilder.registerTypeAdapters(vararg adapters: Pair<Class<*>, Any>): GsonBuilder =
    adapters.forEach { registerTypeAdapter(it.first, it.second) }.let { this }