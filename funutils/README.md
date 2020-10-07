[![](https://jitpack.io/v/jakepurple13/HelpfulTools.svg)](https://jitpack.io/#jakepurple13/HelpfulTools)
```gradle
implementation 'com.github.jakepurple13.HelpfulTools:funutils:{version}'
```

# [FunUtils](https://github.com/jakepurple13/HelpfulTools/blob/master/funutils/src/main/java/com/programmersbox/funutils)

## [Cards](https://github.com/jakepurple13/HelpfulTools/blob/master/funutils/src/main/java/com/programmersbox/funutils/cards)
```kotlin
val c = Card(1, Suit.SPADES)
val d = Deck.defaultDeck() //a deck of Cards
val deckOfStrings = Deck<String>()
deckOfStrings+=listOf("Hello", "World")
```

## [SequenceMaker](https://github.com/jakepurple13/HelpfulTools/blob/master/funutils/src/main/java/com/programmersbox/funutils/funutilities)
```kotlin
val s = SequenceMaker(1, 2, 3, 4, 5, object : SequenceListener {
    override fun onAchieved() {
        
    }   

    override fun onFail() {
    
    }

    override fun onReset() {
    
    }
})
```

## [Views](https://github.com/jakepurple13/HelpfulTools/blob/master/funutils/src/main/java/com/programmersbox/funutils/views)

