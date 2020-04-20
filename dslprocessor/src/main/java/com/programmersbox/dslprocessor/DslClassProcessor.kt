package com.programmersbox.dslprocessor

import com.google.auto.service.AutoService
import com.programmersbox.dslprocessor.APUtils.getTypeMirrorFromAnnotationValue
import com.squareup.kotlinpoet.*
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

            val spec = methodElement.enclosedElements.filterIsInstance<VariableElement>()
                .filter { !it.annotationMirrors.any { it.annotationType.asTypeName() == DslField::class.asTypeName() } }
                .filter { !it.simpleName.contains("Companion") }
                .map {
                    generateNewMethod(it, try {
                        val a: DslClass = methodElement.getAnnotation(DslClass::class.java)
                        getTypeMirrorFromAnnotationValue(object : APUtils.GetClassValue {
                            override fun execute() {
                                a.dslMarker
                            }
                        })?.map { it?.let { (it.asTypeName().javaToKotlinType() as? ClassName) } }
                    } catch (e: Exception) {
                        listOf(DslFieldMarker::class.asClassName())
                    }
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

    private fun generateNewMethod(variable: VariableElement, annotation: List<ClassName?>?): FunSpec {
        return FunSpec
            .builder(variable.simpleName.toString())
            .addModifiers(KModifier.PUBLIC)
            .receiver(variable.enclosingElement.asType().asTypeName())
            .also { builder ->
                try {
                    (variable.enclosingElement as? TypeElement)
                        ?.typeParameters
                        ?.map { TypeVariableName(it.simpleName.toString()) }
                        ?.let { builder.addTypeVariables(it) }
                } catch (e: Exception) {
                }
            }
            .apply { annotation?.forEach { it?.let { it1 -> addAnnotation(it1) } } }
            .addParameter("block", variable.asType().asTypeName().javaToKotlinType2())
            .addStatement("${variable.simpleName} = block")
            .build()
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> = mutableSetOf(DslClass::class.java.canonicalName)
}
