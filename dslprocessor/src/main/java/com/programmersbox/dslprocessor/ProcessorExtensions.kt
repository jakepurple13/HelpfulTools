package com.programmersbox.dslprocessor

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import me.eugeniomarletti.kotlin.metadata.shadow.metadata.ProtoBuf
import me.eugeniomarletti.kotlin.metadata.shadow.metadata.deserialization.NameResolver
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

internal fun isNullableProperty(element: Element): Boolean = element.getAnnotation(org.jetbrains.annotations.Nullable::class.java) != null


internal fun ProtoBuf.TypeParameter.asTypeName(
    nameResolver: NameResolver,
    getTypeParameter: (index: Int) -> ProtoBuf.TypeParameter,
    resolveAliases: Boolean = false
): TypeVariableName {
    return TypeVariableName(
        name = nameResolver.getString(name),
        bounds = *(upperBoundList.map {
            it.asTypeName(nameResolver, getTypeParameter, resolveAliases)
        }
            .toTypedArray()),
        variance = variance.asKModifier()
    )
}

internal fun ProtoBuf.TypeParameter.Variance.asKModifier(): KModifier? {
    return when (this) {
        ProtoBuf.TypeParameter.Variance.IN -> KModifier.IN
        ProtoBuf.TypeParameter.Variance.OUT -> KModifier.OUT
        ProtoBuf.TypeParameter.Variance.INV -> null
    }
}

/**
 * Returns the TypeName of this typeInfo as it would be seen in the source code, including nullability
 * and generic typeInfo parameters.
 *
 * @param [nameResolver] a [NameResolver] instance from the source proto
 * @param [getTypeParameter] a function that returns the typeInfo parameter for the given index. **Only
 *     called if [ProtoBuf.Type.hasTypeParameter] is true!**
 */
internal fun ProtoBuf.Type.asTypeName(
    nameResolver: NameResolver,
    getTypeParameter: (index: Int) -> ProtoBuf.TypeParameter,
    useAbbreviatedType: Boolean = true
): TypeName {

    val argumentList = when {
        useAbbreviatedType && hasAbbreviatedType() -> abbreviatedType.argumentList
        else -> argumentList
    }

    if (hasFlexibleUpperBound()) {
        return WildcardTypeName.consumerOf(
            flexibleUpperBound.asTypeName(nameResolver, getTypeParameter, useAbbreviatedType)
        )
            .asNullableIf(nullable)
    } else if (hasOuterType()) {
        return WildcardTypeName.producerOf(
            outerType.asTypeName(nameResolver, getTypeParameter, useAbbreviatedType)
        )
            .asNullableIf(nullable)
    }

    val realType = when {
        hasTypeParameter() -> return getTypeParameter(typeParameter)
            .asTypeName(nameResolver, getTypeParameter, useAbbreviatedType)
            .asNullableIf(nullable)
        hasTypeParameterName() -> typeParameterName
        useAbbreviatedType && hasAbbreviatedType() -> abbreviatedType.typeAliasName
        else -> className
    }

    var typeName: TypeName =
        ClassName.bestGuess(
            nameResolver.getString(realType)
                .replace("/", ".")
        )

    if (argumentList.isNotEmpty()) {
        val remappedArgs: Array<TypeName> = argumentList.map { argumentType ->
            val nullableProjection = if (argumentType.hasProjection()) {
                argumentType.projection
            } else null
            if (argumentType.hasType()) {
                argumentType.type.asTypeName(nameResolver, getTypeParameter, useAbbreviatedType)
                    .let { argumentTypeName ->
                        nullableProjection?.let { projection ->
                            when (projection) {
                                ProtoBuf.Type.Argument.Projection.IN -> WildcardTypeName.producerOf(argumentTypeName)
                                ProtoBuf.Type.Argument.Projection.OUT -> {
                                    if (argumentTypeName == ANY) {
                                        // This becomes a *, which we actually don't want here.
                                        // List<Any> works with List<*>, but List<*> doesn't work with List<Any>
                                        argumentTypeName
                                    } else {
                                        WildcardTypeName.consumerOf(argumentTypeName)
                                    }
                                }
                                ProtoBuf.Type.Argument.Projection.STAR -> WildcardTypeName.producerOf(ANY)
                                ProtoBuf.Type.Argument.Projection.INV -> TODO("INV projection is unsupported")
                            }
                        } ?: argumentTypeName
                    }
            } else {
                WildcardTypeName.consumerOf(ANY)
            }
        }.toTypedArray()
        typeName = (typeName as ClassName).parameterizedBy(*remappedArgs)
    }

    return typeName.asNullableIf(nullable)
}

internal fun TypeName.asNullableIf(condition: Boolean): TypeName {
    return if (condition) copy(nullable = true) else this
}

internal fun genericTypeNames(proto: ProtoBuf.Class, nameResolver: NameResolver): List<TypeVariableName> {
    return proto.typeParameterList.map {
        val possibleBounds = it.upperBoundList
            .map { it.asTypeName(nameResolver, proto::getTypeParameter, false) }
        val typeVar = if (possibleBounds.isEmpty()) {
            TypeVariableName(
                name = nameResolver.getString(it.name),
                variance = it.varianceModifier
            )
        } else {
            TypeVariableName(
                name = nameResolver.getString(it.name),
                bounds = *possibleBounds.toTypedArray(),
                variance = it.varianceModifier
            )
        }
        return@map typeVar.copy(reified = it.reified)
    }
}

internal val ProtoBuf.TypeParameter.varianceModifier: KModifier?
    get() {
        return variance.asKModifier().let {
            // We don't redeclare out variance here
            if (it == KModifier.OUT) {
                null
            } else {
                it
            }
        }
    }