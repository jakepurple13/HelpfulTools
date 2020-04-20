package com.programmersbox.dslprocessor

import kotlin.reflect.KClass

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class DslClass(val dslMarker: KClass<*> = DslFieldMarker::class)