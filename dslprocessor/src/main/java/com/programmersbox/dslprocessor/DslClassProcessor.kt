package com.programmersbox.dslprocessor

import com.google.auto.service.AutoService
import com.programmersbox.dslannotations.DslClass
import com.programmersbox.dslannotations.DslField
import com.programmersbox.dslannotations.DslFieldMarker
import com.programmersbox.dslprocessor.APUtils.getTypeMirrorFromAnnotationValue
import com.squareup.kotlinpoet.*
import me.eugeniomarletti.kotlin.metadata.KotlinClassMetadata
import me.eugeniomarletti.kotlin.metadata.kotlinMetadata
import me.eugeniomarletti.kotlin.metadata.proto
import me.eugeniomarletti.kotlin.metadata.shadow.metadata.ProtoBuf
import java.io.File
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement

@AutoService(Processor::class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedOptions(DslClassProcessor.KAPT_KOTLIN_GENERATED_OPTION_NAME)
class DslClassProcessor : AbstractProcessor() {

    companion object {
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
    }

    override fun process(annotations: MutableSet<out TypeElement>, roundEnv: RoundEnvironment): Boolean {
        val functions = roundEnv.getElementsAnnotatedWith(DslClass::class.java).mapNotNull { methodElement ->
            //println("--------------------------------------------")

            if (methodElement.kind != ElementKind.CLASS) {
                processingEnv.messager.errormessage { "Can only be applied to classes,  element: $methodElement " }
                return false
            }

            val properties = (methodElement.kotlinMetadata as? KotlinClassMetadata)
                ?.data
                ?.proto
                ?.propertyList
                .orEmpty()

            val elements = methodElement.enclosedElements
                .filterIsInstance<VariableElement>()
                .filter { !it.annotationMirrors.any { it.annotationType.asTypeName() == DslField::class.asTypeName() } }
                .filter { !it.simpleName.contains("Companion") }

            val otherWay = properties
                .intersect(elements) { t, r -> (methodElement.kotlinMetadata as? KotlinClassMetadata)?.data?.nameResolver?.getString(t.name) == r.simpleName.toString() }
                .sortedBy { (methodElement.kotlinMetadata as? KotlinClassMetadata)?.data?.nameResolver?.getString(it.name) }

            val props = elements
                .let {
                    if (methodElement.kotlinMetadata != null)
                        it.intersect(properties) { r, t -> (methodElement.kotlinMetadata as? KotlinClassMetadata)?.data?.nameResolver?.getString(t.name) == r.simpleName.toString() }
                    else elements
                }
                .sortedBy { it.simpleName.toString() }

            val annotationList = try {
                val a: DslClass = methodElement.getAnnotation(DslClass::class.java)
                getTypeMirrorFromAnnotationValue(object : APUtils.GetClassValue {
                    override fun execute() {
                        a.dslMarker
                    }
                })?.map { it?.let { (it.asTypeName().javaToKotlinType() as? ClassName) } }
            } catch (e: Exception) {
                listOf(DslFieldMarker::class.asClassName())
            }
            val spec = props.mapIndexed { index, t ->
                generateNewMethod(
                    t,
                    annotationList,
                    methodElement.kotlinMetadata as? KotlinClassMetadata,
                    otherWay.getOrNull(index)
                )
            }

            if (spec.isNotEmpty()) {
                processingEnv.elementUtils.getPackageOf(methodElement).toString() to spec
            } else null
        }

        val generatedSourcesRoot: String = processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME].orEmpty()
        if (generatedSourcesRoot.isEmpty()) {
            processingEnv.messager.errormessage { "Can't find the target directory for generated Kotlin files." }
            return false
        }

        if (functions.isNotEmpty()) {
            val packageGroup = functions.groupBy { it.first }
            for (g in packageGroup.entries.withIndex()) {
                val file = File(generatedSourcesRoot)
                if (!file.exists()) file.mkdir()
                val fileBuilder = FileSpec.builder(
                    g.value.key,
                    "DslClassGenerated${g.index}"
                )
                g.value.value.forEach { it.second.forEach { fileBuilder.addFunction(it) } }
                fileBuilder
                    .build()
                    //.also { println(it.toString()) }
                    .writeTo(file)
            }
        }
        return false
    }

    private fun generateNewMethod(
        variable: VariableElement,
        annotation: List<ClassName?>?,
        kotlinClass: KotlinClassMetadata? = null,
        property: ProtoBuf.Property?
    ): FunSpec {
        return FunSpec
            .builder(variable.simpleName.toString())
            .addModifiers(KModifier.PUBLIC)
            .receiver(variable.enclosingElement.asType().asTypeName())
            .also { builder ->
                kotlinClass?.let { builder.addTypeVariables(genericTypeNames(it.data.classProto, it.data.nameResolver)) } ?: try {
                    (variable.enclosingElement as? TypeElement)
                        ?.typeParameters
                        ?.map { TypeVariableName(it.simpleName.toString()) }
                        ?.let { builder.addTypeVariables(it) }
                } catch (e: Exception) {
                }
            }
            .apply { annotation?.forEach { it?.let { it1 -> addAnnotation(it1) } } }
            .addParameter(
                "block",
                property?.returnType?.asTypeName(kotlinClass!!.data.nameResolver, kotlinClass.data.classProto::getTypeParameter)
                    ?: variable.asType().asTypeName().javaToKotlinType2().copy(nullable = isNullableProperty(variable))
            )
            .addStatement("${variable.simpleName} = block")
            .build()
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> = mutableSetOf(DslClass::class.java.canonicalName)
}

/**
 * Finds similarities between two lists based on a predicate
 */
internal fun <T, R> Iterable<T>.intersect(uList: Iterable<R>, filterPredicate: (T, R) -> Boolean) =
    filter { m -> uList.any { filterPredicate(m, it) } }