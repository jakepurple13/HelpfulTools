package com.programmersbox.gsonutils

import com.google.gson.JsonDeserializer
import com.google.gson.JsonObject
import com.google.gson.JsonSerializer
import com.programmersbox.helpfulutils.*

/**
 * This was made was for any custom classes in [HelpfulUtils](https://github.com/jakepurple13/HelpfulTools/tree/master/helpfulutils/src/main/java/com/programmersbox/helpfulutils)
 * Supported classes are
 *
 * [FixedList], [FixedSet], [FixedMap], [ItemRange], [MutableItemRange]
 * @see fromJson
 */
inline fun <reified T> String?.fromJsonToHelpful(): T? = fromJson(
    fixedListFromAdapter<T>(),
    fixedSetFromAdapter<T>(),
    fixedMapFromAdapter(),
    itemRangeFromAdapter<T>(),
    mutableItemRangeFromAdapter<T>()
)

/**
 * This was made was for any custom classes in [HelpfulUtils](https://github.com/jakepurple13/HelpfulTools/tree/master/helpfulutils/src/main/java/com/programmersbox/helpfulutils)
 * Supported classes are
 *
 * [FixedList], [FixedSet], [FixedMap], [ItemRange], [MutableItemRange]
 * @see toJson
 */
fun Any?.toHelpfulJson() = toJson(
    fixedListToAdapter(),
    fixedSetToAdapter(),
    fixedMapToAdapter(),
    rangeToAdapter<ItemRange<*>>(),
    rangeToAdapter<MutableItemRange<*>>(),
    rangeToAdapter<NumberRange>()
)

@PublishedApi
internal inline fun <reified T> mutableItemRangeFromAdapter() = MutableItemRange::class.java to JsonDeserializer<MutableItemRange<T>> { j, _, _ ->
    val obj = j.asJsonObject
    MutableItemRange(obj["list"].asString.fromJson<List<T>>().orEmpty().toMutableList()).apply {
        loop = obj["loop"].asBoolean
        current = obj["current"].asInt
    }
}

@PublishedApi
internal inline fun <reified T> itemRangeFromAdapter() = ItemRange::class.java to JsonDeserializer<ItemRange<T>> { j, _, _ ->
    val obj = j.asJsonObject
    ItemRange(obj["list"].asString.fromJson<List<T>>().orEmpty()).apply {
        loop = obj["loop"].asBoolean
        current = obj["current"].asInt
    }
}

internal inline fun <reified T : Range<*>> rangeToAdapter() = T::class.java to JsonSerializer<T> { src, _, _ ->
    JsonObject().apply {
        addProperty("loop", src.loop)
        addProperty("list", src.toJson())
        addProperty("current", src.current)
    }
}

@PublishedApi
internal inline fun <reified T> fixedListFromAdapter() = FixedList::class.java to JsonDeserializer<FixedList<T>> { j, _, _ ->
    val obj = j.asJsonObject
    FixedList(
        obj["maxSize"].asInt,
        obj["removeFrom"].asString.fromJson<FixedListLocation>() ?: FixedListLocation.END,
        obj["list"].asString.fromJson<List<T>>().orEmpty()
    )
}

internal fun fixedListToAdapter() = FixedList::class.java to JsonSerializer<FixedList<*>> { src, _, _ ->
    JsonObject().apply {
        addProperty("maxSize", src.fixedSize)
        addProperty("removeFrom", src.removeFrom.toJson())
        addProperty("list", src.toJson())
    }
}

@PublishedApi
internal inline fun <reified T> fixedSetFromAdapter() = FixedSet::class.java to JsonDeserializer<FixedSet<T>> { j, _, _ ->
    val obj = j.asJsonObject
    FixedSet(
        obj["maxSize"].asInt,
        obj["removeFrom"].asString.fromJson<FixedListLocation>() ?: FixedListLocation.END,
        obj["list"].asString.fromJson<Set<T>>().orEmpty()
    )
}

internal fun fixedSetToAdapter() = FixedSet::class.java to JsonSerializer<FixedSet<*>> { src, _, _ ->
    JsonObject().apply {
        addProperty("maxSize", src.fixedSize)
        addProperty("removeFrom", src.removeFrom.toJson())
        addProperty("list", src.toJson())
    }
}

@PublishedApi
internal fun fixedMapFromAdapter() = FixedMap::class.java to JsonDeserializer<FixedMap<*, *>> { j, _, _ ->
    val obj = j.asJsonObject
    FixedMap(
        obj["maxSize"].asInt,
        obj["removeFrom"].asString.fromJson<FixedListLocation>() ?: FixedListLocation.END,
        obj["list"].asString.fromJson<Map<*, *>>().orEmpty()
    )
}

internal fun fixedMapToAdapter() = FixedMap::class.java to JsonSerializer<FixedMap<*, *>> { src, _, _ ->
    JsonObject().apply {
        addProperty("maxSize", src.fixedSize)
        addProperty("removeFrom", src.removeFrom.toJson())
        addProperty("list", src.toJson())
    }
}