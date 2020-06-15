package com.programmersbox.helpfulutils

import android.app.DownloadManager
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.database.getIntOrNull
import androidx.core.database.getLongOrNull
import androidx.core.database.getStringOrNull
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

@DownloadListener
class DownloadManagerListener internal constructor(private val context: Context) {

    private val downloadManager = context.downloadManager

    private val filterByIds = mutableListOf<Long>()

    fun addId(id: Long) = filterByIds.add(id)

    enum class DownloadStatus(internal val value: Int) {
        FAILED(DownloadManager.STATUS_FAILED),
        PAUSED(DownloadManager.STATUS_PAUSED),
        PENDING(DownloadManager.STATUS_PENDING),
        RUNNING(DownloadManager.STATUS_RUNNING),
        SUCCESSFUL(DownloadManager.STATUS_SUCCESSFUL);

        companion object {
            internal fun getStatusFromValue(id: Int) = values().find { it.value == id }
        }
    }

    enum class DownloadReason(internal val value: Int) {
        QUEUED_FOR_WIFI(DownloadManager.PAUSED_QUEUED_FOR_WIFI),
        WAITING_FOR_NETWORK(DownloadManager.PAUSED_WAITING_FOR_NETWORK),
        WAITING_TO_RETRY(DownloadManager.PAUSED_WAITING_TO_RETRY),
        UNKNOWN(DownloadManager.PAUSED_UNKNOWN);

        companion object {
            internal fun getReasonFromValue(id: Int) = values().find { it.value == id }
        }
    }

    private val filterByStatus = mutableListOf<DownloadStatus>()

    fun addStatus(status: DownloadStatus) = filterByStatus.add(status)

    private var listener: (DownloadInfo) -> Unit = {}

    fun listener(block: (DownloadInfo) -> Unit) = run { listener = block }

    var updateInterval: Long = 500L

    data class DownloadInfo(
        val status: DownloadStatus,
        val title: String,
        val id: Long,
        val description: String,
        val localUri: String?,
        val uri: String?,
        val reason: DownloadReason?,
        val lastModifiedTimestamp: Long,
        val mimeType: String,
        val downloadedSoFar: Int,
        val totalSize: Int
    ) {
        val progress: Long get() = (downloadedSoFar * 100L) / totalSize
        val uriAsUri: Uri get() = Uri.parse(uri)
        val localUriAsUri: Uri get() = Uri.parse(localUri)
    }

    private fun build() {

        DownloadManager.ACTION_DOWNLOAD_COMPLETE
        DownloadManager.ACTION_VIEW_DOWNLOADS
        DownloadManager.ACTION_NOTIFICATION_CLICKED

        val c = downloadManager.query(
            DownloadManager.Query()
                .setFilterById(*filterByIds.toLongArray())
                .also {
                    try {
                        it.setFilterByStatus(
                            filterByStatus.drop(1).fold(filterByStatus[0].value) { acc, downloadStatus -> acc or downloadStatus.value }
                        )
                    } catch (e: Exception) {
                    }
                }
        )

        //DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR
        //DownloadManager.COLUMN_DESCRIPTION
        //DownloadManager.COLUMN_ID
        //DownloadManager.COLUMN_LAST_MODIFIED_TIMESTAMP
        //DownloadManager.COLUMN_LOCAL_URI
        //DownloadManager.COLUMN_MEDIAPROVIDER_URI
        //DownloadManager.COLUMN_MEDIA_TYPE
        //DownloadManager.COLUMN_REASON
        //DownloadManager.COLUMN_STATUS
        //DownloadManager.COLUMN_TITLE
        //DownloadManager.COLUMN_TOTAL_SIZE_BYTES
        //DownloadManager.COLUMN_URI

        val rows = c.count
        var counter = 0

        fun Cursor.longValue(column: String) = getLong(getColumnIndex(column))
        fun Cursor.stringValue(column: String) = getString(getColumnIndex(column))
        fun Cursor.intValue(column: String) = getInt(getColumnIndex(column))

        fun Cursor.longValueOrNull(column: String) = getLongOrNull(getColumnIndex(column))
        fun Cursor.stringValueOrNull(column: String) = getStringOrNull(getColumnIndex(column))
        fun Cursor.intValueOrNull(column: String) = getIntOrNull(getColumnIndex(column))

        Thread(Runnable {
            while (true) {
                if (c != null) {
                    while (c.moveToNext()) {
                        val info = DownloadInfo(
                            status = DownloadStatus.getStatusFromValue(c.intValue(DownloadManager.COLUMN_STATUS)) ?: DownloadStatus.FAILED,
                            title = c.stringValue(DownloadManager.COLUMN_TITLE),
                            id = c.longValue(DownloadManager.COLUMN_ID),
                            description = c.stringValue(DownloadManager.COLUMN_DESCRIPTION),
                            lastModifiedTimestamp = c.longValue(DownloadManager.COLUMN_LAST_MODIFIED_TIMESTAMP),
                            localUri = c.stringValueOrNull(DownloadManager.COLUMN_LOCAL_URI),
                            uri = c.stringValueOrNull(DownloadManager.COLUMN_URI),
                            mimeType = c.stringValue(DownloadManager.COLUMN_MEDIA_TYPE),
                            reason = DownloadReason.getReasonFromValue(c.intValue(DownloadManager.COLUMN_REASON)),
                            downloadedSoFar = c.intValue(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR),
                            totalSize = c.intValue(DownloadManager.COLUMN_TOTAL_SIZE_BYTES)
                        )
                        listener(info)
                        if (info.status == DownloadStatus.SUCCESSFUL || info.status == DownloadStatus.FAILED) counter++
                        //Thread.sleep(updateInterval)
                    }
                }
                println("Counter: $counter and Rows: $rows")
                if (counter >= rows) break
                Thread.sleep(updateInterval)
            }
            c.close()
        }).start()
    }

    companion object {
        @DownloadListener
        operator fun invoke(context: Context, block: DownloadManagerListener.() -> Unit) = DownloadManagerListener(context).apply(block).build()
    }
}

@RequiresOptIn
annotation class DownloadListener