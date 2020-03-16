package com.programmersbox.gsonutils

import android.content.Intent
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken

/**
 * put any object into the intent
 * This converts the object into a json string
 */
fun <T> Intent.putExtra(key: String, value: T): Intent = putExtra(key, Gson().toJson(value))

/**
 * get the object
 */
inline fun <reified T> Intent.getObjectExtra(key: String, defaultValue: T): T = try {
    Gson().fromJson(getStringExtra(key), T::class.java) ?: defaultValue
} catch (e: Exception) {
    defaultValue
}

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
 * Update an object that's in sharedpreferences
 */
inline fun <reified T> SharedPreferences.updateObject(key: String, defaultValue: T? = null, block: T.() -> Unit) =
    edit().putObject(key, getObject<T>(key, defaultValue)?.apply(block)).apply()

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
 *
 * Takes [this] and converts it to a json string based on type adapters
 *
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
fun Any?.toJson(vararg adapters: Pair<Class<*>, Any>): String = GsonBuilder().registerTypeAdapters(*adapters).create().toJson(this)

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

/**
 * An easy way to register type adapters
 */
fun GsonBuilder.registerTypeAdapters(vararg adapters: Pair<Class<*>, Any>): GsonBuilder =
    adapters.forEach { registerTypeAdapter(it.first, it.second) }.let { this }