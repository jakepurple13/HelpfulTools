[![](https://jitpack.io/v/jakepurple13/HelpfulTools.svg)](https://jitpack.io/#jakepurple13/HelpfulTools)
```gradle
implementation 'com.github.jakepurple13.HelpfulTools:loggingutils:{version}'
```

# [Logging](https://github.com/jakepurple13/HelpfulTools/tree/master/loggingutils/src/main/java/com/programmersbox/loggingutils)

Here we have some utilities for logging!

Using this module also includes a custom lint rule to highlight default android log statements to the new loggingutils ones.

```kotlin
//Default Android Logs
Log.w("Hello", "World")
Log.wtf("Hello", "World")
Log.i("Hello", "World")
Log.v("Hello", "World")
Log.e("Hello", "World")
Log.d("Hello", "World")
//These will do the new logging logs
Loged.w("Hello World")
Loged.a("Hello World")
Loged.i("Hello World")
Loged.v("Hello World")
Loged.e("Hello World")
Loged.d("Hello World")
Loged.wtf("Hello World")
Loged.r("Hello World")
//These will put a box around the log
//These are extensions
Loged.f("Hello World")
Loged.fw("Hello World")
Loged.fa("Hello World")
Loged.fi("Hello World")
Loged.fv("Hello World")
Loged.fe("Hello World")
Loged.fd("Hello World")
```

These are the outputs of the code above

```
W/Hello: World
E/Hello: World
I/Hello: World
V/Hello: World
E/Hello: World
D/Hello: World



W/HelpfulTools/main: Hello World
    ╚═▷	com.programmersbox.helpfultools.MainActivity$onCreate$4.onClick(MainActivity.kt:77)
A/HelpfulTools/main: Hello World
    ╚═▷	com.programmersbox.helpfultools.MainActivity$onCreate$4.onClick(MainActivity.kt:78)
I/HelpfulTools/main: Hello World
    ╚═▷	com.programmersbox.helpfultools.MainActivity$onCreate$4.onClick(MainActivity.kt:79)
V/HelpfulTools/main: Hello World
    ╚═▷	com.programmersbox.helpfultools.MainActivity$onCreate$4.onClick(MainActivity.kt:80)
E/HelpfulTools/main: Hello World
    ╚═▷	com.programmersbox.helpfultools.MainActivity$onCreate$4.onClick(MainActivity.kt:81)
D/HelpfulTools/main: Hello World
    ╚═▷	com.programmersbox.helpfultools.MainActivity$onCreate$4.onClick(MainActivity.kt:82)
A/HelpfulTools/main: Hello World
    ╚═▷	com.programmersbox.helpfultools.MainActivity$onCreate$4.onClick(MainActivity.kt:83)
D/HelpfulTools/main: Hello World
    ╚═▷	com.programmersbox.helpfultools.MainActivity$onCreate$4.onClick(MainActivity.kt:84)



E/HelpfulTools/main: HelpfulTools
    ╔=HelpfulTools=╗
    ║ Hello World  ║
    ╠==============╝
    ╚═▷	com.programmersbox.helpfultools.MainActivity$onCreate$4.onClick(MainActivity.kt:86)
W/HelpfulTools/main: HelpfulTools
    ╔=HelpfulTools=╗
    ║ Hello World  ║
    ╠==============╝
    ╚═▷	com.programmersbox.helpfultools.MainActivity$onCreate$4.onClick(MainActivity.kt:87)
A/HelpfulTools/main: HelpfulTools
    ╔=HelpfulTools=╗
    ║ Hello World  ║
    ╠==============╝
    ╚═▷	com.programmersbox.helpfultools.MainActivity$onCreate$4.onClick(MainActivity.kt:88)
I/HelpfulTools/main: HelpfulTools
    ╔=HelpfulTools=╗
    ║ Hello World  ║
    ╠==============╝
    ╚═▷	com.programmersbox.helpfultools.MainActivity$onCreate$4.onClick(MainActivity.kt:89)
V/HelpfulTools/main: HelpfulTools
    ╔=HelpfulTools=╗
    ║ Hello World  ║
    ╠==============╝
    ╚═▷	com.programmersbox.helpfultools.MainActivity$onCreate$4.onClick(MainActivity.kt:90)
E/HelpfulTools/main: HelpfulTools
    ╔=HelpfulTools=╗
    ║ Hello World  ║
    ╠==============╝
    ╚═▷	com.programmersbox.helpfultools.MainActivity$onCreate$4.onClick(MainActivity.kt:91)
D/HelpfulTools/main: HelpfulTools
    ╔=HelpfulTools=╗
    ║ Hello World  ║
    ╠==============╝
    ╚═▷	com.programmersbox.helpfultools.MainActivity$onCreate$4.onClick(MainActivity.kt:92)
```

# [LogedInterceptor](https://github.com/jakepurple13/HelpfulTools/blob/master/loggingutils/src/main/java/com/programmersbox/loggingutils/LogedInterceptor.kt)
I added a LogedInterceptor so you can save logs to a file if you wish
```kotlin
class Interceptor : LogedInterceptor {
    override fun log(level: LogLevel, tag: String, msg: String) {
        //save to file or other actions with the log here
        println("${level.name[0]}/$tag/$msg")
    }
}
```
