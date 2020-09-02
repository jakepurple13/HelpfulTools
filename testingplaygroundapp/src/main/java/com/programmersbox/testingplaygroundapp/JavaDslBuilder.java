package com.programmersbox.testingplaygroundapp;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;

//@DslClass(dslMarker = JavaDslMarker.class)
public class JavaDslBuilder {
    int num = 4;

    //@DslField(name = "javaName", comment = "This is a java one")
    String name = "";

    Function0<Unit> function = () -> null;

    //@DslField(name = "functionOne", comment = "This is a java one")
    Function0<Unit> function1 = () -> null;

    static void javaDslBuild(Function1<JavaDslBuilder, Unit> block) {
        JavaDslBuilder dsl = new JavaDslBuilder();
        block.invoke(dsl);
        dsl.function.invoke();
        dsl.function1.invoke();
    }
}
