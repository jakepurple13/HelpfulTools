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

```

## [Notification](https://github.com/jakepurple13/HelpfulTools/blob/master/helpfulutils/src/main/java/com/programmersbox/helpfulutils/NotificationUtils.kt)
```kotlin

createNotificationChannel("testChannel")
createNotificationGroup("testGroup")

//NotificationDslBuilder has support for below O and above O
val notification = NotificationDslBuilder.builder(this) {
    title = "Title"
    message = "Message"
    channelId = "testChannel"
    smallIconId = R.mipmap.ic_launcher
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
}

notificationManager.notify(/*notification id*/, notification)

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
