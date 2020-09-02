package com.programmersbox.testingplaygroundapp

@DslMarker
annotation class PersonMarker

@DslMarker
annotation class PersonMarker2

data class Person(val name: String, var age: Int, private val birthday: (Int) -> Int) {
    fun birthday() {
        age = birthday(age)
    }
}

class PersonBuilder {

    //@DslField("personName", PersonMarker::class)
    var name: String = ""

    //@DslField("personAge", PersonMarker::class)
    var age = 0

    //@DslField("birthdayParty", PersonMarker::class)
    var birthday: (Int) -> Int = { it }

    private fun build() = Person(name, age, birthday)

    companion object {
        fun builder(block: PersonBuilder.() -> Unit) = PersonBuilder().apply(block).build()
    }

}

//@DslClass(PersonMarker2::class)
class PersonBuilder2 {
    //@DslField("birthdayParty", comment = "Set what happens on his birthday party")
    var birthday: (Int) -> Int = { it }
    var name: String = ""
    var age = 0

    private fun build() = Person(name, age, birthday)

    companion object {
        fun builder(block: PersonBuilder2.() -> Unit) = PersonBuilder2().apply(block).build()
    }

}

class PersonBuilder3 {

    private var birthday: (Int) -> Int = { it }

    @PersonMarker2
    fun birthdayParty(block: (Int) -> Int) {
        birthday = block
    }

    private var name: String = ""

    @PersonMarker2
    fun name(s: String) {
        name = s
    }

    private var age = 0

    @PersonMarker2
    fun age(num: Int) {
        age = num
    }

    private fun build() = Person(name, age, birthday)

    companion object {
        fun builder(block: PersonBuilder3.() -> Unit) = PersonBuilder3().apply(block).build()
    }

}