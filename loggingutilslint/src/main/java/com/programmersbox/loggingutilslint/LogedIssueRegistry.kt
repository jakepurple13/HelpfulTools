package com.programmersbox.loggingutilslint

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.detector.api.CURRENT_API

/*
 * The list of issues that will be checked when running <code>lint</code>.
 */
@Suppress("UnstableApiUsage")
class LogedIssueRegistry : IssueRegistry() {
    override val issues = AndroidLogDetector.issues

    override val api: Int get() = CURRENT_API
}