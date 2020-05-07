[![](https://jitpack.io/v/jakepurple13/HelpfulTools.svg)](https://jitpack.io/#jakepurple13/HelpfulTools)
```groovy
implementation 'com.github.jakepurple13.HelpfulTools:gsonutils:{version}'
```

# [Gson](https://github.com/jakepurple13/HelpfulTools/blob/master/gsonutils/src/main/java/com/programmersbox/gsonutils/GsonUtils.kt)

Super easy way to convert objects to json and json string's to objects!

To use:
```kotlin
data class AnotherObject(val item: String)
data class GsonObject(val string: String, val int: Int, val anotherObject: AnotherObject)

val item = GsonObject("Hello", 5, AnotherObject("World"))
val jsonString: String = item.toJson()
val jsonPrettyString: String = item.toPrettyJson()
val gsonObject: GsonObject = jsonString.fromJson<GsonObject>()
```

Also added a couple other functions for Intents and SharedPreferences:
```kotlin
val intent = Intent(/*...*/).apply {
   putExtra("key", item)
}
val item2 = intent.getObjectExtra<GsonObject>("key", null)

getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE).edit().putObject("gson", item).apply()
val item3 = getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE).getObject<GsonObject>("gson")
```


Along with some [network](https://github.com/jakepurple13/HelpfulTools/blob/master/gsonutils/src/main/java/com/programmersbox/gsonutils/ApiUtils.kt) methods:
```kotlin
val string = getApi("api url")
val apiObject = getJsonApi<GsonObject>("api url")
```


Also added a method to tie in with the [SharedPrefDelegate](https://github.com/jakepurple13/HelpfulTools/blob/master/helpfulutils/src/main/java/com/programmersbox/helpfulutils/ContextUtils.kt#L103) called [sharedPrefObjectDelegate](https://github.com/jakepurple13/HelpfulTools/blob/master/gsonutils/src/main/java/com/programmersbox/gsonutils/GsonUtils.kt#L120:
```kotlin
var Context.gsonObject: GsonObject? by sharedPrefObjectDelegate()
```