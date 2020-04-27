package com.programmersbox.dslprocessor

import com.google.auto.service.AutoService
import com.programmersbox.dslannotations.DslField
import com.programmersbox.dslannotations.DslFieldMarker
import com.programmersbox.dslprocessor.APUtils.getTypeMirrorFromAnnotationValue
import com.squareup.kotlinpoet.*
import me.eugeniomarletti.kotlin.metadata.KotlinClassMetadata
import me.eugeniomarletti.kotlin.metadata.kotlinMetadata
import me.eugeniomarletti.kotlin.metadata.proto
import java.io.File
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement

@AutoService(Processor::class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedOptions(DslFieldsProcessor.KAPT_KOTLIN_GENERATED_OPTION_NAME)
class DslFieldsProcessor : AbstractProcessor() {

    companion object {
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
    }

    private val annotation = DslField::class.java

    override fun process(annotations: MutableSet<out TypeElement>, roundEnv: RoundEnvironment): Boolean {
        val functions = roundEnv.getElementsAnnotatedWith(annotation).mapNotNull { methodElement ->
            //println("--------------------------------------------")

            if (methodElement.kind != ElementKind.FIELD) {
                processingEnv.messager.errormessage { "Can only be applied to variables,  element: $methodElement " }
                return false
            }

            val spec = (methodElement as? VariableElement)?.let { generateNewMethod(it) }

            if (spec != null) {
                processingEnv.elementUtils.getPackageOf(methodElement).toString() to spec
            } else null
        }

        val generatedSourcesRoot: String = processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME].orEmpty()
        if (generatedSourcesRoot.isEmpty()) {
            processingEnv.messager.errormessage { "Can't find the target directory for generated Kotlin files." }
            return false
        }

        //println(functions.joinToString("\n"))

        if (functions.isNotEmpty()) {
            val packageGroup = functions.groupBy { it.first }
            for (g in packageGroup.entries.withIndex()) {
                val file = File(generatedSourcesRoot)
                if (!file.exists()) file.mkdir()
                val fileBuilder = FileSpec.builder(
                    g.value.key,
                    "DslFieldsGenerated${g.index}"
                )
                g.value.value.forEach { fileBuilder.addFunction(it.second) }
                fileBuilder
                    .build()
                    //.also { println(it.toString()) }
                    .writeTo(file)
            }
        }
        return false
    }

    private fun generateNewMethod(variable: VariableElement): FunSpec {
        val annotationInfo = variable.getAnnotation(annotation)
        return FunSpec
            .builder(annotationInfo.name)
            .addModifiers(KModifier.PUBLIC)
            .receiver(variable.enclosingElement.asType().asTypeName())
            .addKdoc(annotationInfo.comment)
            .also { builder ->
                val classData = (variable.enclosingElement.kotlinMetadata as? KotlinClassMetadata)?.data
                classData?.let { builder.addTypeVariables(genericTypeNames(it.classProto, it.nameResolver)) } ?: try {
                    (variable.enclosingElement as? TypeElement)
                        ?.typeParameters
                        ?.map { TypeVariableName(it.simpleName.toString()) }
                        ?.let { builder.addTypeVariables(it) }
                } catch (e: Exception) {
                }
            }
            .also { builder ->
                try {
                    val a: DslField = annotationInfo
                    getTypeMirrorFromAnnotationValue(object : APUtils.GetClassValue {
                        override fun execute() {
                            a.dslMarker
                        }
                    })?.forEach { it?.let { (it.asTypeName().javaToKotlinType() as? ClassName)?.let { builder.addAnnotation(it) } } }
                } catch (e: Exception) {
                    builder.addAnnotation(DslFieldMarker::class)
                }
            }
            .also { builder ->
                val classData = (variable.enclosingElement.kotlinMetadata as? KotlinClassMetadata)?.data
                val property = classData?.proto?.propertyList?.find { classData.nameResolver.getString(it.name) == variable.simpleName.toString() }
                builder.addParameter(
                    "block",
                    property?.returnType?.asTypeName(classData.nameResolver, classData.classProto::getTypeParameter)
                        ?: variable.asType().asTypeName().javaToKotlinType2().copy(nullable = isNullableProperty(variable))
                )
            }
            .addStatement("${variable.simpleName} = block")
            .build()
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> = mutableSetOf(annotation.canonicalName)
}
