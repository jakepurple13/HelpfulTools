package com.programmersbox.gsonutils

import com.google.gson.*
import com.programmersbox.helpfulutils.*
import java.lang.reflect.Type

/**
 * This was made was for any custom classes in [HelpfulUtils](https://github.com/jakepurple13/HelpfulTools/tree/master/helpfulutils/src/main/java/com/programmersbox/helpfulutils)
 * Supported classes are
 *
 * [FixedList], [FixedSet], [FixedMap], [ItemRange], [MutableItemRange]
 * @see fromJson
 */
inline fun <reified T> String?.fromJsonToHelpful(): T? = fromJson(
    FixedList::class.java to FixedListAdapter(),
    FixedSet::class.java to FixedSetAdapter(),
    FixedMap::class.java to FixedMapAdapter(),
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
    FixedList::class.java to FixedListAdapter(),
    FixedSet::class.java to FixedSetAdapter(),
    FixedMap::class.java to FixedMapAdapter(),
    rangeToAdapter<ItemRange<*>>(),
    rangeToAdapter<MutableItemRange<*>>()
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
        addProperty("current", src.current)
        addProperty("list", src.toJson())
        if (src is NumberRange) addProperty("step", src.step)
    }
}

@PublishedApi
internal class FixedListAdapter : JsonSerializer<FixedList<*>>, JsonDeserializer<FixedList<*>> {

    override fun serialize(src: FixedList<*>, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement = JsonObject().apply {
        addProperty("maxSize", src.fixedSize)
        addProperty("removeFrom", src.removeFrom.name)
        addProperty("list", src.toJson())
    }

    override fun deserialize(json: JsonElement, typeOfT: Type?, context: JsonDeserializationContext?): FixedList<*> {
        val obj = json.asJsonObject
        return FixedList(
            obj["maxSize"].asInt,
            obj.getFixedLocation(),
            obj["list"].asString.fromJson<List<*>>().orEmpty()
        )
    }
}

@PublishedApi
internal class FixedSetAdapter : JsonSerializer<FixedSet<*>>, JsonDeserializer<FixedSet<*>> {

    override fun serialize(src: FixedSet<*>, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement = JsonObject().apply {
        addProperty("maxSize", src.fixedSize)
        addProperty("removeFrom", src.removeFrom.name)
        addProperty("list", src.toJson())
    }

    override fun deserialize(json: JsonElement, typeOfT: Type?, context: JsonDeserializationContext?): FixedSet<*> {
        val obj = json.asJsonObject
        return FixedSet(
            obj["maxSize"].asInt,
            obj.getFixedLocation(),
            obj["list"].asString.fromJson<Set<*>>().orEmpty()
        )
    }

}

@PublishedApi
internal class FixedMapAdapter : JsonSerializer<FixedMap<*, *>>, JsonDeserializer<FixedMap<*, *>> {
    override fun deserialize(json: JsonElement, typeOfT: Type?, context: JsonDeserializationContext?): FixedMap<*, *> {
        val obj = json.asJsonObject
        return FixedMap(
            obj["maxSize"].asInt,
            obj.getFixedLocation(),
            obj["list"].asString.fromJson<Map<*, *>>().orEmpty()
        )
    }

    override fun serialize(src: FixedMap<*, *>, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement = JsonObject().apply {
        addProperty("maxSize", src.fixedSize)
        addProperty("removeFrom", src.removeFrom.name)
        addProperty("list", src.toJson())
    }
}

internal fun JsonObject.getFixedLocation() = try {
    FixedListLocation.valueOf(this["removeFrom"].asString)
} catch (e: Exception) {
    FixedListLocation.END
}