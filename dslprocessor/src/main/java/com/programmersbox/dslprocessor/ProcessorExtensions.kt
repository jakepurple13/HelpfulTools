package com.programmersbox.dslprocessor

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import me.eugeniomarletti.kotlin.metadata.shadow.name.FqName
import me.eugeniomarletti.kotlin.metadata.shadow.platform.JavaToKotlinClassMap
import javax.annotation.processing.Messager
import javax.lang.model.element.Element
import javax.lang.model.type.MirroredTypeException
import javax.lang.model.type.MirroredTypesException
import javax.lang.model.type.TypeMirror


object APUtils {
    fun getTypeMirrorFromAnnotationValue(c: GetClassValue): List<TypeMirror?>? {
        try {
            c.execute()
        } catch (ex: MirroredTypesException) {
            return ex.typeMirrors
        }
        return null
    }

    @FunctionalInterface
    interface GetClassValue {
        @Throws(MirroredTypeException::class, MirroredTypesException::class)
        fun execute()
    }
}

fun Messager.errormessage(message: () -> String) {
    this.printMessage(javax.tools.Diagnostic.Kind.ERROR, message())
}

fun Messager.noteMessage(message: () -> String) {
    this.printMessage(javax.tools.Diagnostic.Kind.NOTE, message())
}

fun Messager.warningMessage(message: () -> String) {
    this.printMessage(javax.tools.Diagnostic.Kind.WARNING, message())
}

fun Element.javaToKotlinType(): TypeName = asType().asTypeName().javaToKotlinType()

fun TypeName.javaToKotlinType(): TypeName {
    return if (this is ParameterizedTypeName) {
        (rawType.javaToKotlinType() as ClassName).parameterizedBy(*typeArguments.map { it.javaToKotlinType() }.toTypedArray())
    } else {
        val className = JavaToKotlinClassMap.mapJavaToKotlin(FqName(toString()))?.asSingleFqName()?.asString()
        return if (className == null) {
            this
        } else {
            ClassName.bestGuess(className)
        }
    }
}

fun TypeName.javaToKotlinType2(): TypeName {
    return when (this) {
        is ParameterizedTypeName ->
            (rawType.javaToKotlinType2() as ClassName).parameterizedBy(*typeArguments.map { it.javaToKotlinType2() }.toTypedArray())
        is WildcardTypeName ->
            if (inTypes.isNotEmpty())
                WildcardTypeName.consumerOf(inTypes[0].javaToKotlinType2())
            else
                WildcardTypeName.producerOf(outTypes[0].javaToKotlinType2())
        else -> {
            val className = JavaToKotlinClassMap.mapJavaToKotlin(FqName(toString()))?.asSingleFqName()?.asString()
            if (className == null) {
                this
            } else {
                ClassName.bestGuess(className)
            }
        }
    }
}