package com.programmersbox.dslannotations

import kotlin.reflect.KClass

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.FIELD)
annotation class DslField(val name: String, val dslMarker: KClass<*> = DslFieldMarker::class, val comment: String = "")

@DslMarker
annotation class DslFieldMarker