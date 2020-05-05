[![](https://jitpack.io/v/jakepurple13/HelpfulTools.svg)](https://jitpack.io/#jakepurple13/HelpfulTools)
```gradle
implementation 'com.github.jakepurple13.HelpfulTools:flowutils:{version}'
```
# [FlowUtils](https://github.com/jakepurple13/HelpfulTools/tree/master/flowutils/src/main/java/com/programmersbox/flowutils)

## [FlowItem](https://github.com/jakepurple13/HelpfulTools/blob/master/flowutils/src/main/java/com/programmersbox/flowutils/FlowItem.kt)
This acts like an Rx PublishSubject but uses Flow.
```kotlin
val item = FlowItem(4)
item.setValue(5)
item.collectOnUI { println(it) }
item(6)
val num: Int = item()
```

## [UI Flow](https://github.com/jakepurple13/HelpfulTools/blob/master/flowutils/src/main/java/com/programmersbox/flowutils/UIFlowExtensions.kt)
These are just like RxBindings.
```kotlin
view.clicks().collectOnUi { println("Button was pressed") }
```
