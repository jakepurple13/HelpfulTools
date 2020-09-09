package com.programmersbox.gsonutils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import com.programmersbox.helpfulutils.defaultSharedPref
import com.programmersbox.helpfulutils.sharedPrefDelegate
import com.programmersbox.helpfulutils.sharedPrefNotNullDelegate
import com.programmersbox.helpfulutils.sharedPrefNotNullDelegateSync
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * put any object into the intent
 * This converts the object into a json string
 */
fun <T> Intent.putExtra(key: String, value: T): Intent = putExtra(key, Gson().toJson(value))

/**
 * get the object
 */
inline fun <reified T> Intent.getObjectExtra(key: String, defaultValue: T? = null): T? = try {
    getStringExtra(key).fromJson<T>() ?: defaultValue
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
    apply { adapters.forEach { registerTypeAdapter(it.first, it.second) } }

/**
 * Difference between this and [sharedPrefDelegate] is that this automatically uses [SharedPreferences.getObject] and [SharedPreferences.Editor.putObject]
 * @see sharedPrefDelegate
 */
inline fun <reified T> sharedPrefObjectDelegate(
    defaultValue: T? = null,
    key: String? = null,
    noinline getter: SharedPreferences.(key: String, defaultValue: T?) -> T? = { k, d -> getObject(k, d) },
    noinline setter: SharedPreferences.Editor.(key: String, value: T?) -> SharedPreferences.Editor = SharedPreferences.Editor::putObject,
    noinline prefs: Context.() -> SharedPreferences = { defaultSharedPref }
) = sharedPrefDelegate(prefs = prefs, key = key, getter = getter, setter = setter, defaultValue = defaultValue)

/**
 * Difference between this and [sharedPrefNotNullDelegate] is that this automatically uses [SharedPreferences.getObject] and [SharedPreferences.Editor.putObject]
 * @see sharedPrefNotNullDelegate
 */
inline fun <reified T> sharedPrefNotNullObjectDelegate(
    defaultValue: T,
    key: String? = null,
    noinline getter: SharedPreferences.(key: String, defaultValue: T) -> T = { k, d -> getObject(k, d)!! },
    noinline setter: SharedPreferences.Editor.(key: String, value: T) -> SharedPreferences.Editor = SharedPreferences.Editor::putObject,
    noinline prefs: Context.() -> SharedPreferences = { defaultSharedPref }
) = sharedPrefNotNullDelegate(prefs = prefs, key = key, getter = getter, setter = setter, defaultValue = defaultValue)

/**
 * Difference between this and [sharedPrefNotNullObjectDelegate] is that this will commit the changes synchronously
 * @see sharedPrefNotNullObjectDelegate
 */
inline fun <reified T> sharedPrefNotNullObjectDelegateSync(
    defaultValue: T,
    key: String? = null,
    noinline getter: SharedPreferences.(key: String, defaultValue: T) -> T = { k, d -> getObject(k, d)!! },
    noinline setter: SharedPreferences.Editor.(key: String, value: T) -> SharedPreferences.Editor = SharedPreferences.Editor::putObject,
    noinline prefs: Context.() -> SharedPreferences = { defaultSharedPref }
) = sharedPrefNotNullDelegateSync(prefs = prefs, key = key, getter = getter, setter = setter, defaultValue = defaultValue)

/**
 * A way so that you can set global variables instead of needing to initialize them in onCreate
 * this calls
 */
inline fun <reified T> intentDelegate(
    key: String? = null,
    crossinline getter: Intent.(key: String, defaultValue: T?) -> T? = { k, d -> getObjectExtra(k, d) }
) = object : ReadOnlyProperty<Activity, T?> {
    private val keys: KProperty<*>.() -> String get() = { key ?: name }
    private var value: T? = null
    override operator fun getValue(thisRef: Activity, property: KProperty<*>): T? {
        if (value == null) value = thisRef.intent.getter(property.keys(), null)
        return value
    }
}