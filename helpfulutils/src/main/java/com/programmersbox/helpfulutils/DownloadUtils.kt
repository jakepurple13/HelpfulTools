package com.programmersbox.helpfulutils

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import kotlin.properties.Delegates

fun DownloadManager.enqueue(context: Context, block: DownloadDslManager.() -> Unit) = enqueue(DownloadDslManager(context, block))

@DownloadMarker
fun DownloadManager.requestBuilder(context: Context, block: DownloadDslManager.() -> Unit) = DownloadDslManager(context, block)

@DslMarker
annotation class DownloadMarker

class DownloadDslManager internal constructor(private val context: Context) {

    /**
     * The uri to download
     */
    @DownloadMarker
    var downloadUri: Uri by Delegates.notNull()

    /**
     * The uri to download
     */
    @DownloadMarker
    fun downloadUrl(url: String) = run { downloadUri = Uri.parse(url) }

    /**
     * @see DownloadManager.Request.setTitle
     */
    @DownloadMarker
    var title: CharSequence? = null

    private val headers = mutableMapOf<String, String>()

    /**
     * @see DownloadManager.Request.addRequestHeader
     */
    @DownloadMarker
    fun addHeader(pair: Pair<String, String>) = run { headers[pair.first] = pair.second }

    /**
     * @see DownloadManager.Request.addRequestHeader
     */
    @DownloadMarker
    fun addHeader(key: String, value: String) = addHeader(key to value)

    /**
     * @see DownloadManager.Request.setDescription
     */
    @DownloadMarker
    var description: CharSequence? = null

    /**
     * @see DownloadManager.Request.setRequiresDeviceIdle
     */
    @RequiresApi(Build.VERSION_CODES.N)
    @DownloadMarker
    var requiresDeviceIdle: Boolean = false

    /**
     * @see DownloadManager.Request.setRequiresCharging
     */
    @RequiresApi(Build.VERSION_CODES.N)
    @DownloadMarker
    var requiresCharging: Boolean = false

    /**
     * @see DownloadManager.Request.setMimeType
     */
    @DownloadMarker
    var mimeType: String? = null

    enum class NetworkType(internal val value: Int) {
        WIFI(DownloadManager.Request.NETWORK_WIFI),
        MOBILE(DownloadManager.Request.NETWORK_MOBILE),
        WIFI_MOBILE(WIFI.value or MOBILE.value)
    }

    /**
     * @see DownloadManager.Request.setAllowedNetworkTypes
     */
    @DownloadMarker
    var networkType: NetworkType = NetworkType.WIFI_MOBILE

    /**
     * @see DownloadManager.Request.setAllowedOverRoaming
     */
    @DownloadMarker
    var allowOverRoaming: Boolean = true

    /**
     * @see DownloadManager.Request.setAllowedOverMetered
     */
    @DownloadMarker
    var allowOverMetered: Boolean = true

    enum class NotificationVisibility(internal val value: Int) {
        HIDDEN(DownloadManager.Request.VISIBILITY_HIDDEN),
        VISIBLE(DownloadManager.Request.VISIBILITY_VISIBLE),
        COMPLETED(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED),
        ONLY_COMPLETION(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_ONLY_COMPLETION)
    }

    /**
     * @see DownloadManager.Request.setNotificationVisibility
     */
    @DownloadMarker
    var visibility: NotificationVisibility = NotificationVisibility.VISIBLE

    /**
     * @see DownloadManager.Request.setDestinationUri
     */
    @DownloadMarker
    var destinationUri: Uri? = null

    /**
     * @see DownloadManager.Request.setDestinationUri
     */
    @DownloadMarker
    fun destinationUri(path: String) = run { destinationUri = Uri.parse(path) }

    private var destinationInExternalFilesDir: Pair<String, String>? = null

    /**
     * @see DownloadManager.Request.setDestinationInExternalFilesDir
     */
    @DownloadMarker
    fun destinationInExternalFilesDir(dirType: String, subPath: String) = run { destinationInExternalFilesDir = dirType to subPath }

    private var destinationInExternalPublicDir: Pair<String, String>? = null

    /**
     * @see DownloadManager.Request.setDestinationInExternalPublicDir
     */
    @DownloadMarker
    fun destinationInExternalPublicDir(dirType: String, subPath: String) = run { destinationInExternalPublicDir = dirType to subPath }

    private fun build(): DownloadManager.Request = DownloadManager.Request(downloadUri)
        .setAllowedNetworkTypes(networkType.value)
        .setAllowedOverRoaming(allowOverRoaming)
        .setAllowedOverMetered(allowOverMetered)
        .also { r -> headers.forEach { r.addRequestHeader(it.key, it.value) } }
        .whatIfNotNull(title) { setTitle(it) }
        .whatIfNotNull(description) { setDescription(it) }
        .also { if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) it.setRequiresDeviceIdle(requiresDeviceIdle) }
        .also { if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) it.setRequiresCharging(requiresCharging) }
        .whatIfNotNull(mimeType) { setMimeType(it) }
        .setNotificationVisibility(visibility.value)
        .whatIfNotNull(destinationUri) { setDestinationUri(it) }
        .whatIfNotNull(destinationInExternalFilesDir) { setDestinationInExternalFilesDir(context, it.first, it.second) }
        .whatIfNotNull(destinationInExternalPublicDir) { setDestinationInExternalPublicDir(it.first, it.second) }

    companion object {
        @DownloadMarker
        operator fun invoke(context: Context, block: DownloadDslManager.() -> Unit) = DownloadDslManager(context).apply(block).build()
    }
}

/*

class DownloadManagerListener internal constructor(private val context: Context) {

    private val downloadManager = context.downloadManager

    private val filterByIds = mutableListOf<Long>()

    enum class DownloadStatus(internal val value: Int) {
        FAILED(DownloadManager.STATUS_FAILED),
        PAUSED(DownloadManager.STATUS_PAUSED),
        PENDING(DownloadManager.STATUS_PENDING),
        RUNNING(DownloadManager.STATUS_RUNNING),
        SUCCESSFUL(DownloadManager.STATUS_SUCCESSFUL)
    }

    private val filterByStatus = mutableListOf<DownloadStatus>()

    private fun build() {
        val c = downloadManager.query(
            DownloadManager.Query()
                .setFilterById(*filterByIds.toLongArray())
                .also {
                    try {
                        it.setFilterByStatus(
                            filterByStatus.drop(1).fold(filterByStatus[0].value) { acc, downloadStatus -> acc or downloadStatus.value })
                    } catch (e: Exception) {
                    }
                }
        )

        DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR
        DownloadManager.COLUMN_DESCRIPTION
        DownloadManager.COLUMN_ID
        DownloadManager.COLUMN_LAST_MODIFIED_TIMESTAMP
        DownloadManager.COLUMN_LOCAL_URI
        DownloadManager.COLUMN_MEDIAPROVIDER_URI
        DownloadManager.COLUMN_MEDIA_TYPE
        DownloadManager.COLUMN_REASON
        DownloadManager.COLUMN_STATUS
        DownloadManager.COLUMN_TITLE
        DownloadManager.COLUMN_TOTAL_SIZE_BYTES
        DownloadManager.COLUMN_URI

    }
}*/
