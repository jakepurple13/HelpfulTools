[![](https://jitpack.io/v/jakepurple13/HelpfulTools.svg)](https://jitpack.io/#jakepurple13/HelpfulTools)
```groovy
//To allow kapt
apply plugin: 'kotlin-kapt'
//For the Annotations
implementation 'com.github.jakepurple13.HelpfulTools:dslannotations:{version}'
//For the actual generation
kapt "com.github.jakepurple13.HelpfulTools:dslprocessor:{version}"
```

### [Dsl Processor](https://github.com/jakepurple13/HelpfulTools/tree/master/dslprocessor/src/main/java/com/programmersbox/dslprocessor)

### [Dsl Annotations](https://github.com/jakepurple13/HelpfulTools/tree/master/dslannotations/src/main/java/com/programmersbox/dslannotations)

Example:
```kotlin

@DslMarker
annotation class PersonMarker

@DslMarker
annotation class PersonMarker2

data class Person(val name: String, var age: Int, private val birthday: (Int) -> Int) {
    fun birthday() {
        age = birthday(age)
    }
}

@DslClass(PersonMarker2::class)
class PersonBuilder {
    @DslField("birthdayParty", comment = "Set what happens on his birthday party")
    var birthday: (Int) -> Int = { it }
    @DslField(dslMarker = PersonMarker::class)
    var name: String = ""
    var age = 0

    private fun build() = Person(name, age, birthday)

    companion object {
        fun builder(block: PersonBuilder.() -> Unit) = PersonBuilder().apply(block).build()
    }

}

```

Generates:
```kotlin
/**
 * Set what happens on his birthday party
 */
@DslFieldMarker
fun PersonBuilder.birthdayParty(block: Function1<Int, Int>) {
  birthday = block
}

@PersonMarker
fun PersonBuilder.name(block: String) {
  name = block
}

@PersonMarker2
fun PersonBuilder.age(block: Int) {
  age = block
}

```

This even works in Java:
```java

@DslMarker
public @interface JavaDslMarker {

}

@DslClass(dslMarker = JavaDslMarker.class)
public class JavaDslBuilder {
    int num = 4;

    @DslField(name = "javaName", comment = "This is a java one")
    String name = "";

    Function0<Unit> function = () -> null;

    @DslField(name = "functionOne", comment = "This is a java one")
    Function0<Unit> function1 = () -> null;

    static void javaDslBuild(Function1<JavaDslBuilder, Unit> block) {
        JavaDslBuilder dsl = new JavaDslBuilder();
        block.invoke(dsl);
        dsl.function.invoke();
        dsl.function1.invoke();
    }
}
```

Generates:
```java
@JavaDslMarker
fun JavaDslBuilder.function(block: Function0<Unit>) {
  function = block
}

@JavaDslMarker
fun JavaDslBuilder.num(block: Int) {
  num = block
}

/**
 * This is a java one
 */
@DslFieldMarker
fun JavaDslBuilder.javaName(block: String) {
  name = block
}

/**
 * This is a java one
 */
@DslFieldMarker
fun JavaDslBuilder.functionOne(block: Function0<Unit>) {
  function1 = block
}

```
