[![](https://jitpack.io/v/jakepurple13/HelpfulTools.svg)](https://jitpack.io/#jakepurple13/HelpfulTools)
```gradle
implementation 'com.github.jakepurple13.HelpfulTools:helpfulutils:{version}'
```

There are lots of utilities here. These are ones that don't fit into the other categories but aren't considered "fun".

# [HelpfulUtils](https://github.com/jakepurple13/HelpfulTools/tree/master/helpfulutils/src/main/java/com/programmersbox/helpfulutils)

## [Permissions](https://github.com/jakepurple13/HelpfulTools/blob/master/helpfulutils/src/main/java/com/programmersbox/helpfulutils/ActivityUtils.kt)
```kotlin
requestPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE) {
    println(it.isGranted)
    println(it.grantedPermissions)
    println(it.deniedPermissions)
}
```

## [Biometric](https://github.com/jakepurple13/HelpfulTools/blob/master/helpfulutils/src/main/java/com/programmersbox/helpfulutils/BiometricUtils.kt)
```kotlin
BiometricBuilder.biometricBuilder(/*FragmentActivity*/) {

    authSuccess {
        "Success"
    }

    authError { _, _ ->
        "Error"
    }

    authFailed {
        "Failed"
    }

    error {
        "Error"
    }

    promptInfo {
        title = "Testing"
        subtitle = "Tester"
        description = "Test"
        negativeButton = null
        confirmationRequired = true
        deviceCredentialAllowed = true
    }
}
```

## [Broadcast Receivers](https://github.com/jakepurple13/HelpfulTools/blob/master/helpfulutils/src/main/java/com/programmersbox/helpfulutils/BroadcastReceiverUtils.kt)
```kotlin

val time: BroadcastReceiver = Context.timeTick { _, _ -> println("Runs every minute") }

val batteryInfo: BroadcastReceiver = Context.battery { info: Battery -> println(info) } 

val screenOff: BroadcastReceiver = Context.screenOff { _,_ -> println("Screen turned off") } 
val screenOn: BroadcastReceiver = Context.screenOn { _,_ -> println("Screen turned on") } 

val screenState: BroadcastReceiver = Context.screenState {
   when(it) {
      ScreenState.OFF -> {}
      ScreenState.ON -> {}
      ScreenState.UNKNOWN -> {}
   }
}

```

## [Activity](https://github.com/jakepurple13/HelpfulTools/blob/master/helpfulutils/src/main/java/com/programmersbox/helpfulutils/ActivityUtils.kt)
```kotlin

ComponentActivity.enableImmersiveMode()
ComponentActivity.hideSystemUI()
ComponentActivity.showSystemUI()
ComponentActivity.addSecureFlag()
ComponentActivity.clearSecureFlag()

ThemeSetting.SYSTEM
ThemeSetting.LIGHT
ThemeSetting.NIGHT

//an easy way to get/set the wanted setting. Uses AppCompatDelegate.setDefaultNightMode(setting.type)
val theme: ThemeSetting? = ThemeSetting.currentThemeSetting
ThemeSetting.setTheme(ThemeSetting.SYSTEM)

```

## [Context](https://github.com/jakepurple13/HelpfulTools/blob/master/helpfulutils/src/main/java/com/programmersbox/helpfulutils/ContextUtils.kt)
```kotlin

var Context.key: String? by sharedPrefDelegate()

val Context.sharedPref get() = defaultSharedPref

Context.speechToText(object : SpeechListener {
    //The arraylist is all the possibilities. Usually the first index is the closest but just in case, here's all of them
    override fun getResult(text: ArrayList<String>?) = println(text)
})

Context.textToSpeech("Hello World")

//Runs on ui thread without the need for Context
runOnUiThread {  }

```

## [Notification](https://github.com/jakepurple13/HelpfulTools/blob/master/helpfulutils/src/main/java/com/programmersbox/helpfulutils/NotificationUtils.kt)
```kotlin

createNotificationChannel("testChannel")
createNotificationGroup("testGroup")

//NotificationDslBuilder has support for below O and above O
val notification = NotificationDslBuilder.builder(this, channelId = "testChannel", smallIconId = R.mipmap.ic_launcher) {
    title = "Title"
    message = "Message"
    autoCancel = true
    addReplyAction {
        resultKey = "result"
        label = "label"
        actionTitle = "Action Title"
        actionIcon = R.mipmap.ic_launcher
        pendingActivity(ReplyService::class.java)
    }
    bigTextStyle {
        summaryText = "Summary"
        contentTitle = "Content Title"
        bigText = "Big Text"
    }
    //To add a bubble
    //Will only work on versions that support it
    addBubble {
        val target = Intent(context, BubbleActivity::class.java)
        val bubbleIntent = PendingIntent.getActivity(context, 0, target, 0 /* flags */)
        bubbleIntent(bubbleIntent)
        desiredHeight = 600
        icon = Icon.createWithResource(context, R.mipmap.ic_launcher)
    }
    //To add a custom NotificationView
    remoteViews {
        landscapeCollapsed(this@MainActivity.packageName, R.layout.collapsed_notification_landscape) {
            setProgressBar(R.id.progress_bar, 100, 2, false)
        }
        portraitCollapsed(this@MainActivity.packageName, R.layout.collapsed_notification_portrait)
        landscapeHeadsUp(this@MainActivity.packageName, R.layout.heads_up_notification_landscape)
        portraitHeadsUp(this@MainActivity.packageName, R.layout.heads_up_notification_portrait)
        landscapeExpanded(this@MainActivity.packageName, R.layout.expanded_notification_landscape)
        portraitExpanded(this@MainActivity.packageName, R.layout.expanded_notification_portrait)
    }
}

notificationManager.notify(/*notification id*/ 1, notification)

```

## [QuickAdapter](https://github.com/jakepurple13/HelpfulTools/blob/master/helpfulutils/src/main/java/com/programmersbox/helpfulutils/QuickAdapter.kt)
You can dynamically add items at anytime
```kotlin
recyclerView.quickAdapter(R.layout.support_simple_spinner_dropdown_item, "Hello", "World") {
    //this is to render the view
    println(it)
}
recyclerView.quickAdapter(R.layout.support_simple_spinner_dropdown_item, "Jake", "Purple") {
    //this is to render the view
    println(it)
}
```

## [ItemRange](https://github.com/jakepurple13/HelpfulTools/blob/master/helpfulutils/src/main/java/com/programmersbox/helpfulutils/RangeUtils.kt)
```kotlin
//this will loop around the items given to it
var range = ItemRange(1, 2, 3, 4, 5, loop = true)
range++
```

## [Collection](https://github.com/jakepurple13/HelpfulTools/blob/master/helpfulutils/src/main/java/com/programmersbox/helpfulutils/CollectionUtils.kt)
```kotlin

val intList = mutableListOf(1, 2, 3)

//added a varargs addAll
intList.addAll(4, 5, 6)

data class ItemOne(val num: Int)

//creates a list of size. Usually helpful for testing data
val itemList = sizedListOf<ItemOne>(10) { ItemOne(it) }

itemList.intersect(intList) { item, i -> i == item.num }
    .let { println(it) } 
//will print [ItemOne(num=1), ItemOne(num=2), ItemOne(num=3), ItemOne(num=4), ItemOne(num=5), ItemOne(num=6)]

val randomInt = intList.randomRemove() //will randomly remove an element
val randomPredicateInt = intList.randomRemove { it % 2 == 0 } //will randomly remove an element based on a predicate
val randomPredicate = intList.random { it % 2 != 0 } //will randomly get an element based on a predicate
val randomNList = intList.randomN(3) //will randomly get N elements and return a list
val randomNRemoveList = intList.randomNRemove(3) //will randomly get N elements and return a list

//FixedList will only hold up to X items. Once capacity is reached, it will remove items from location which can be START or END
val fixedList = FixedList(size = 10, location = FixedListLocation.END, c = listOf(1, 2, 3))
//Same as FixedList but for sets
val fixedSet = FixedSet(size = 10, location = FixedListLocation.END, c = setOf(1, 2, 3))
//Same as FixedList but for maps
val fixedMap = FixedMap(size = 10, location = FixedListLocation.END, c = mapOf(1 to "Hello", 2 to "World", 3 to "!"))

```

## [DateUtils](https://github.com/jakepurple13/HelpfulTools/blob/master/helpfulutils/src/main/java/com/programmersbox/helpfulutils/DateUtils.kt)
```kotlin

//My own implementation of the Duration/TimeUnit classes
HelpfulUnit.HOURS.convert(1, HelpfulUnit.MINUTES)
1.hours.inMinutes
//It takes the same amount of time as Duration/TimeUnit to do the conversions

```

## [DownloadUtils](https://github.com/jakepurple13/HelpfulTools/blob/master/helpfulutils/src/main/java/com/programmersbox/helpfulutils/DownloadUtils.kt)
```kotlin

//an easy dsl way to use the default download manager
downloadManager.enqueue(Context) {
    downloadUri = Uri.parse("downloadUrl")
    allowOverRoaming = true
    networkType = DownloadDslManager.NetworkType.WIFI_MOBILE
    title = "Title"
    mimeType = "image/jpeg"
    visibility = DownloadDslManager.NotificationVisibility.COMPLETED
    destinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, filename)
}

```

## [Utils](https://github.com/jakepurple13/HelpfulTools/blob/master/helpfulutils/src/main/java/com/programmersbox/helpfulutils/Utils.kt)
These are utils I couldn't classify
```kotlin

val c = Random.nextColor() //returns a random color

```

## [ViewUtils](https://github.com/jakepurple13/HelpfulTools/blob/master/helpfulutils/src/main/java/com/programmersbox/helpfulutils/ViewUtils.kt)
```kotlin

TextView.startDrawable = Drawable
TextView.endDrawable = Drawable
TextView.topDrawable = Drawable
TextView.bottomDrawable = Drawable

val b = View.asBitmap() //creates a bitmap based off of the view

val c = Context.colorFromTheme(R.attr.customColor, Color.BLACK) //gets a color based off of a theme

//These three will set the visibility
View.gone()
View.invisible()
View.visible()

ViewGroup.animateChildren {
    //uses transition manager to give default animations.
    //for example; if you use View.gone(), it will animate the view going away without the need to do it yourself
}

Drawable.changeDrawableColor(Color.BLUE) //will mutate and add a color filter

View.hideKeyboard()
View.showKeyboard()

AlertDialog.Builder(Context)
    .setCustomTitle(R.layout.customTitle) { /*setup the title view here*/ }
    .setView(R.layout.customView) { /*setup the view here*/ }
    .setEnumItems(*ViewEnums.values().map { it.name }.toTypedArray()) { e, d ->
        //e is the enum. It does go by ordinal
    }
    
```