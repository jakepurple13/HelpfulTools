package com.programmersbox.thirdpartyutils

import android.content.Context
import android.graphics.Rect
import android.net.Uri
import android.os.Parcel
import android.text.Spannable
import android.text.Spanned
import android.text.method.TransformationMethod
import android.text.style.URLSpan
import android.text.util.Linkify
import android.view.View
import android.widget.TextView
import androidx.browser.customtabs.CustomTabsIntent

/**
 * Open a [url] in a [CustomTabsIntent]
 * Default exit animations are [android.R.anim.slide_in_left] and [android.R.anim.slide_out_right]
 * They can be overridden
 * @param build make any changes to the custom tab intent
 */
fun Context.openInCustomChromeBrowser(url: Uri, build: CustomTabsIntent.Builder.() -> Unit = {}) = CustomTabsIntent.Builder()
    .setExitAnimations(this, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
    .apply(build)
    .build().launchUrl(this, url)

/**
 * @see openInCustomChromeBrowser
 */
fun Context.openInCustomChromeBrowser(url: String, build: CustomTabsIntent.Builder.() -> Unit = {}) = openInCustomChromeBrowser(Uri.parse(url), build)

private class CustomTabsURLSpan : URLSpan {
    private val context: Context
    private val builder: CustomTabsIntent.Builder.() -> Unit

    constructor(url: String?, context: Context, build: CustomTabsIntent.Builder.() -> Unit = {}) : super(url) {
        this.context = context
        this.builder = build
    }

    constructor(src: Parcel, context: Context, build: CustomTabsIntent.Builder.() -> Unit = {}) : super(src) {
        this.context = context
        this.builder = build
    }

    override fun onClick(widget: View) {
        context.openInCustomChromeBrowser(url, builder)
        // attempt to open with custom tabs, if that fails, call super.onClick
    }
}

/**
 * Use this to allow [TextView] links to be open ini a CustomTab
 */
class ChromeCustomTabTransformationMethod(private val context: Context, private val build: CustomTabsIntent.Builder.() -> Unit = {}) :
    TransformationMethod {
    override fun getTransformation(source: CharSequence, view: View?): CharSequence {
        if (view is TextView) {
            Linkify.addLinks(view, Linkify.WEB_URLS)
            if (view.text == null || view.text !is Spannable) return source
            val text: Spannable = view.text as Spannable
            val spans: Array<URLSpan> = text.getSpans(0, view.length(), URLSpan::class.java)
            for (i in spans.indices.reversed()) {
                val oldSpan = spans[i]
                val start: Int = text.getSpanStart(oldSpan)
                val end: Int = text.getSpanEnd(oldSpan)
                val url = oldSpan.url
                text.removeSpan(oldSpan)
                text.setSpan(CustomTabsURLSpan(url, context, build), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            return text
        }
        return source
    }

    override fun onFocusChanged(
        view: View?,
        sourceText: CharSequence?,
        focused: Boolean,
        direction: Int,
        previouslyFocusedRect: Rect?
    ) = Unit
}