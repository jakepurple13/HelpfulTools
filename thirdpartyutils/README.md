[![](https://jitpack.io/v/jakepurple13/HelpfulTools.svg)](https://jitpack.io/#jakepurple13/HelpfulTools)
```groovy
implementation 'com.github.jakepurple13.HelpfulTools:thirdpartyutils:{version}'
```

These are some ThirdParty Utils that I use every now and then when I use the libraries.

# [Lottie](https://github.com/jakepurple13/HelpfulTools/blob/master/thirdpartyutils/src/main/java/com/programmersbox/thirdpartyutils/LottieUtils.kt)
```kotlin
//Easy way to change the tint
com.airbnb.lottie.LottieAnimationView.changeTint(android.graphics.Color.BLUE)

//Easy ways to check if the view's progress is 1f or 0f
com.airbnb.lottie.LottieAnimationView.checked = true
if(com.airbnb.lottie.LottieAnimationView.checked) println("Its true!")
//will automatically animate the view from currentProgress-1f if true or currentProgress-0f if false
com.airbnb.lottie.LottieAnimationView.check(true)
```

# [Palette](https://github.com/jakepurple13/HelpfulTools/blob/master/thirdpartyutils/src/main/java/com/programmersbox/thirdpartyutils/PaletteUtils.kt)
```kotlin
//Get the palette from any drawable easily
android.graphics.drawable.Drawable.getPalette()
```

# [Glide](https://github.com/jakepurple13/HelpfulTools/blob/master/thirdpartyutils/src/main/java/com/programmersbox/thirdpartyutils/GlideUtils.kt)
```kotlin
//An easy to
Glide.with(context)
    .asBitmap() //Whatever this is
    .load(url)
    .into<Bitmap> { //this has to match
        loadCleared { } //You don't need to put this one in
        resourceReady { image, _ -> println("Image is a bitmap!") }
    }
```

# [CustomChromeTabs](https://github.com/jakepurple13/HelpfulTools/blob/master/thirdpartyutils/src/main/java/com/programmersbox/thirdpartyutils/BrowserUtils.kt)
```kotlin
//Opens a url in a custom chrome tab
Context.openInCustomChromeBrowser("https://www.google.com") {
    //modify the tab
}

//Also have it working for TextViews
val tv = TextView(context)

tv.transformationMethod = ChromeCustomTabTransformationMethod(context) {
    //modify the tab
    setStartAnimations(context, R.anim.slide_in_right, R.anim.slide_out_left)
}
tv.movementMethod = LinkMovementMethod.getInstance()
```

# [Retrofit](https://github.com/jakepurple13/HelpfulTools/blob/master/thirdpartyutils/src/main/java/com/programmersbox/thirdpartyutils/RetrofitUtils.kt)
```kotlin
//wip
```