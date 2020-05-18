package com.programmersbox.testingplaygroundapp

import kotlin.reflect.KCallable
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KParameter
import kotlin.reflect.KProperty
import kotlin.reflect.full.*

class ClassInfo(clazz: Class<*>) {
    sealed class ClassInfoItems(val name: String, val type: String, val visibility: String?, val mutability: String) {
        override fun toString(): String = visibility?.toLowerCase()?.let { "$it " } ?: ""
        protected val toParamType = { p: PropertyInfo -> "${p.name}: ${p.type}" }

        class PropertyInfo(name: String, type: String, visibility: String? = null, mutability: String) :
            ClassInfoItems(name, type, visibility, mutability) {
            override fun toString(): String = "${super.toString()}$mutability $name: $type"
        }

        class MethodInfo(name: String, returnType: String, visibility: String? = null, vararg val parameters: PropertyInfo) :
            ClassInfoItems(name, returnType, visibility, "fun") {
            override fun toString(): String = "${super.toString()}$mutability $name(${parameters.joinToString(transform = toParamType)}): $type"
        }

        class ConstructorInfo(name: String, visibility: String? = null, vararg val parameters: PropertyInfo) :
            ClassInfoItems(name, name, visibility, "") {
            override fun toString(): String = "${super.toString()}$name(${parameters.joinToString(transform = toParamType)})"
        }
    }

    private val toProps: (KCallable<*>) -> Array<ClassInfoItems.PropertyInfo> = { it.parameters.mapNotNullTryCatch(paramToProp).toTypedArray() }
    private val nameCon: (KCallable<*>) -> String = { "${it.extensionReceiverParameter?.type?.let { type -> "$type." } ?: ""}${it.name}" }
    private val propConvert: (KProperty<*>) -> ClassInfoItems.PropertyInfo = {
        ClassInfoItems.PropertyInfo(nameCon(it), it.returnType.toString(), it.visibility?.toString(), if (it is KMutableProperty<*>) "var" else "val")
    }
    private val paramToProp: (KParameter) -> ClassInfoItems.PropertyInfo? =
        { p -> ClassInfoItems.PropertyInfo(p.name!!, p.type.toString(), null, "") }
    val className: String = clazz.simpleName
    val superClasses: List<ClassInfo> = clazz.kotlin.superclasses.filter { it.simpleName != "Any" }.mapNotNullTryCatch { ClassInfo(it.java) }
    val constructors: List<ClassInfoItems.ConstructorInfo> = clazz.kotlin.constructors.map {
        ClassInfoItems.ConstructorInfo(it.name, it.visibility?.toString(), *toProps(it))
    }
    val subClasses: List<ClassInfo> = clazz.classes.map { ClassInfo(it) }
    val propertyList: List<ClassInfoItems.PropertyInfo> = clazz.kotlin.memberProperties.map(propConvert) +
            clazz.kotlin.memberExtensionProperties.map(propConvert) + clazz.kotlin.staticProperties.map(propConvert)
    val methodList: List<ClassInfoItems.MethodInfo> = clazz.kotlin.functions.map {
        ClassInfoItems.MethodInfo(nameCon(it), it.returnType.toString(), it.visibility?.toString(), *toProps(it))
    }
    val enumSet = clazz.enumConstants?.map { it } ?: emptyList()
    override fun toString(): String = className
    private fun <T, R : Any> List<T>.mapNotNullTryCatch(transform: (T) -> R?) = mapNotNull {
        try {
            transform(it)
        } catch (e: Exception) {
            null
        }
    }
}

@Suppress("TestFunctionName")
inline fun <reified T> ClassInfo() = ClassInfo(T::class.java)