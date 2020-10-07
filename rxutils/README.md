[![](https://jitpack.io/v/jakepurple13/HelpfulTools.svg)](https://jitpack.io/#jakepurple13/HelpfulTools)
```gradle
implementation 'com.github.jakepurple13.HelpfulTools:rxutils:{version}'
```

# [RxUtils](https://github.com/jakepurple13/HelpfulTools/blob/master/rxutils/src/main/java/com/programmersbox/rxutils)

```kotlin
val publish = PublishSubject.create<String>()
publish
    .ioMain()
    .doOnError { println(it) }
    .doOnComplete { println("Done") }
    .subscribe { println(it) }
//added some some invoke functions
//calls onNext
publish("Hello")
//calls onComplete
publish()
//calls onError
publish(Throwable("Error!"))

//calls the requestPermissions method in HelpfulUtils but wraps it in a Single
FragmentActivity.rxRequestPermissions(Manifest.permission.RECORD_AUDIO)
                     .doOnError { it.printStackTrace() }
                     .subscribeBy { println(it) }
                     .addTo(disposable)

//added a delegate to BehaviorSubject so that you set/get a variable that references a BehaviorSubject
val publish = BehaviorSubject.create<String>()
publish
    .doOnError { println(it) }
    .subscribe { println(it) }
var item: String? by behaviorDelegate(publish)
var item2: String? by publish.toDelegate()
println(item)
item = "Hello"
item = "World"
println(item)
```