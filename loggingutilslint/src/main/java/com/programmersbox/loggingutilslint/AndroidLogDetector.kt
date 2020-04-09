package com.programmersbox.loggingutilslint

import com.android.tools.lint.detector.api.*
import com.android.tools.lint.detector.api.Detector.UastScanner
import com.intellij.psi.PsiMethod
import org.jetbrains.uast.UCallExpression
import org.jetbrains.uast.UElement
import org.jetbrains.uast.ULiteralExpression


@Suppress("UnstableApiUsage")
class AndroidLogDetector : Detector(), UastScanner {

    override fun getApplicableMethodNames(): List<String> = listOf("v", "d", "i", "w", "e", "wtf")

    override fun getApplicableUastTypes(): List<Class<out UElement?>> = listOf(ULiteralExpression::class.java)

    override fun visitMethod(context: JavaContext, node: UCallExpression, method: PsiMethod) {
        if (context.evaluator.isMemberInClass(method, "android.util.Log")) {
            context.report(ISSUE_LOG, node, context.getLocation(node), "Using 'Log' instead of 'Loged'", quickFixIssueLog(node))
            return
        }
    }

    private fun quickFixIssueLog(logCall: UCallExpression): LintFix {
        val arguments = logCall.valueArguments
        val methodName = logCall.methodName.let { if (it == "wtf") "a" else it }
        val tag = arguments[0].asSourceString()
        val className = "Loged."
        val msgOrThrowable = arguments[1]
        val fixes = listOf(
            "$className$methodName(${msgOrThrowable.asSourceString()}, $tag)",
            "$className$methodName(${msgOrThrowable.asSourceString()})",
            "${className}r(${msgOrThrowable.asSourceString()}, $tag)",
            "${className}r(${msgOrThrowable.asSourceString()})",
            "${className}f$methodName(${msgOrThrowable.asSourceString()}, $tag)",
            "${className}f$methodName(${msgOrThrowable.asSourceString()})",
            "${className}f(${msgOrThrowable.asSourceString()}, $tag)",
            "${className}f(${msgOrThrowable.asSourceString()})"
        )
        val logCallSource = logCall.asSourceString()
        val fixGrouper = fix().group()
        fun addToGroup(s: String) = fixGrouper.add(fix().replace().all().reformat(true).with(s).build())
        fixes.forEach { addToGroup(it) }
        return fixGrouper.build()
    }

    /*override fun createUastHandler(context: JavaContext): UElementHandler? {
        // Note: Visiting UAST nodes is a pretty general purpose mechanism;
        // Lint has specialized support to do common things like "visit every class
        // that extends a given super class or implements a given interface", and
        // "visit every call site that calls a method by a given name" etc.
        // Take a careful look at UastScanner and the various existing lint check
        // implementations before doing things the "hard way".
        // Also be aware of context.getJavaEvaluator() which provides a lot of
        // utility functionality.
        return object : UElementHandler() {
            override fun visitLiteralExpression(node: ULiteralExpression) {
                val string = node.evaluateString() ?: return
                if (string.contains("lint") && string.matches(Regex(".*\\blint\\b.*"))) {
                    context.report(
                        ISSUE, node, context.getLocation(node),
                        "This code mentions `lint`: **Congratulations**"
                    )
                }
            }
        }
    }*/

    companion object {

        val issues
            get() = listOf(
                ISSUE_LOG//, ISSUE
            )

        /** Issue describing the problem and pointing to the detector implementation */
        @JvmField
        val ISSUE: Issue = Issue.create(
            // ID: used in @SuppressLint warnings etc
            id = "ShortUniqueId",
            // Title -- shown in the IDE's preference dialog, as category headers in the
            // Analysis results window, etc
            briefDescription = "Lint Mentions",
            // Full explanation of the issue; you can use some markdown markup such as
            // `monospace`, *italic*, and **bold**.
            explanation = """
                    This check highlights string literals in code which mentions the word `lint`. \
                    Blah blah blah.
                    Another paragraph here.
                    """, // no need to .trimIndent(), lint does that automatically
            category = Category.CORRECTNESS,
            priority = 6,
            severity = Severity.WARNING,
            implementation = Implementation(
                AndroidLogDetector::class.java,
                Scope.JAVA_FILE_SCOPE
            )
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