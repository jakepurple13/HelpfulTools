package com.programmersbox.loggingutilslint

import com.android.tools.lint.detector.api.*
import com.android.tools.lint.detector.api.Detector.UastScanner
import com.intellij.psi.PsiMethod
import org.jetbrains.uast.UCallExpression
import org.jetbrains.uast.UElement
import org.jetbrains.uast.ULiteralExpression
import org.jetbrains.uast.UQualifiedReferenceExpression

@Suppress("UnstableApiUsage")
class AndroidLogDetector : Detector(), UastScanner {

    override fun getApplicableMethodNames(): List<String> = listOf("v", "d", "i", "w", "e", "wtf")

    override fun getApplicableUastTypes(): List<Class<out UElement?>> = listOf(ULiteralExpression::class.java)

    override fun visitMethodCall(context: JavaContext, node: UCallExpression, method: PsiMethod) {
        if (context.evaluator.isMemberInClass(method, "android.util.Log")) {
            context.report(ISSUE_LOG, node, context.getLocation(node), "Using 'Log' instead of 'Loged'", quickFixIssueLog(node))
            return
        }
    }

    private fun quickFixIssueLog(logCall: UCallExpression): LintFix {
        val arguments = logCall.valueArguments
        val methodName = logCall.methodName
        val frameMethodName = methodName.let { if (it == "wtf") "a" else it }
        val tag = arguments[0].asSourceString()
        //val isJava = isJava(arguments[1].lang)
        val isKotlin = isKotlin(arguments[1].lang)
        val className = "Loged."
        val msgOrThrowable = arguments[1].let {
            when (it) {
                is UQualifiedReferenceExpression -> it.sourcePsi?.text ?: it.asSourceString()
                else -> it.asSourceString()
            }.removeSuffix(".toString()")
        }

        val fixes = listOf(
            "$className$methodName($msgOrThrowable, $tag)",
            "${className}r($msgOrThrowable, $tag)",
            "$className$methodName($msgOrThrowable)",
            "${className}r($msgOrThrowable)",
            *(if (isKotlin) listOf(
                "${className}f$frameMethodName($msgOrThrowable, $tag)",
                "${className}f$frameMethodName($msgOrThrowable)",
                "${className}f($msgOrThrowable, $tag)",
                "${className}f($msgOrThrowable)"
            ) else emptyList()).toTypedArray()
        )
        val fixGrouper = fix().group()
        fixGrouper.join(*fixes.map { fix().replace().all().reformat(true).with(it).build() }.toTypedArray())
        return fixGrouper.build()
    }

    companion object {

        val issues
            get() = listOf(
                ISSUE_LOG
            )

        val ISSUE_LOG = Issue.create(
            "LogNotLoged",
            "Logging call to Log instead of Loged",
            "Since Loged is included in the project, it is likely that calls to Log should instead be going to Loged.",
            Category.MESSAGES,
            5,
            Severity.WARNING,
            Implementation(AndroidLogDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }
}