package com.programmersbox.helpfulutils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationChannelGroup
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.session.MediaSession
import android.os.Build
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.RemoteInput
import androidx.core.app.TaskStackBuilder
import kotlin.properties.Delegates

enum class NotificationChannelImportance(internal val importance: Int) {
    /**
     * A notification with no importance: does not show in the shade.
     */
    NONE(0),

    /**
     * Min notification importance: only shows in the shade, below the fold.  This should
     * not be used with {@link Service#startForeground(int, Notification) Service.startForeground}
     * since a foreground service is supposed to be something the user cares about so it does
     * not make semantic sense to mark its notification as minimum importance.  If you do this
     * as of Android version {@link android.os.Build.VERSION_CODES#O}, the system will show
     * a higher-priority notification about your app running in the background.
     */
    MIN(1),

    /**
     * Low notification importance: Shows in the shade, and potentially in the status bar
     * (see {@link #shouldHideSilentStatusBarIcons()}), but is not audibly intrusive.
     */
    LOW(2),

    /**
     * Default notification importance: shows everywhere, makes noise, but does not visually
     * intrude.
     */
    DEFAULT(3),

    /**
     * Higher notification importance: shows everywhere, makes noise and peeks. May use full screen
     * intents.
     */
    HIGH(4),

    /**
     * Unused.
     */
    MAX(5)
}

/**
 * Creates a [NotificationChannel]
 */
@RequiresApi(Build.VERSION_CODES.O)
fun Context.createNotificationChannel(
    id: String,
    name: CharSequence = id,
    importance: NotificationChannelImportance = NotificationChannelImportance.DEFAULT,
    block: NotificationChannel.() -> Unit = {}
) = notificationManager.createNotificationChannel(NotificationChannel(id, name, importance.importance).apply(block))

/**
 * Creates a [NotificationChannelGroup]
 */
@RequiresApi(Build.VERSION_CODES.O)
fun Context.createNotificationGroup(id: String, name: CharSequence = id, block: NotificationChannelGroup.() -> Unit = {}) =
    notificationManager.createNotificationChannelGroup(NotificationChannelGroup(id, name).apply(block))

/**
 * sendNotification - sends a notification
 * @param smallIconId the icon id for the notification
 * @param title the title
 * @param message the message
 * @param channelId the channel id
 * @param gotoActivity the activity that will launch when notification is pressed
 * @param notificationId the id of the notification
 */
fun Context.sendNotification(
    @DrawableRes smallIconId: Int,
    title: String?,
    message: String?,
    notificationId: Int,
    channelId: String,
    groupId: String = channelId,
    gotoActivity: Class<*>? = null
) {
    // mNotificationId is a unique integer your app uses to identify the
    // notification. For example, to cancel the notification, you can pass its ID
    // number to NotificationManager.cancel().
    notificationManager.notify(
        notificationId,
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) oAndGreaterNotification(smallIconId, title, message, channelId, groupId, gotoActivity)
        else compatNotification(smallIconId, title, message, channelId, groupId, gotoActivity)
    )
}

@RequiresApi(Build.VERSION_CODES.O)
private fun Context.oAndGreaterNotification(
    @DrawableRes smallIconId: Int,
    title: String?,
    message: String?,
    channelId: String,
    groupId: String,
    gotoActivity: Class<*>? = null
): Notification = Notification.Builder(this, channelId)
    .setSmallIcon(smallIconId)
    .setContentTitle(title)
    .setContentText(message)
    .setGroup(groupId)
    .setContentIntent(pendingActivity(gotoActivity))
    .build()

private fun Context.compatNotification(
    @DrawableRes smallIconId: Int,
    title: String?,
    message: String?,
    channelId: String,
    groupId: String,
    gotoActivity: Class<*>? = null
): Notification = NotificationCompat.Builder(this, channelId)
    .setSmallIcon(smallIconId)
    .setContentTitle(title)
    .setContentText(message)
    .setGroup(groupId)
    .setContentIntent(pendingActivity(gotoActivity))
    .build()

private fun Context.pendingActivity(gotoActivity: Class<*>?) = gotoActivity?.let {
    TaskStackBuilder.create(this)
        .addParentStack(gotoActivity)
        .addNextIntent(Intent(this, it))
        .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
}

fun Context.sendNotification(notificationId: Int, block: NotificationDslBuilder.() -> Unit) =
    notificationManager.notify(notificationId, NotificationDslBuilder.builder(this, block))

@DslMarker
annotation class NotificationUtilsMarker

@DslMarker
annotation class NotificationActionMarker

@DslMarker
annotation class NotificationStyleMarker

class NotificationDslBuilder(private val context: Context) {

    @NotificationUtilsMarker
    var channelId: String by Delegates.notNull()

    @NotificationUtilsMarker
    var groupId: String = ""

    @NotificationUtilsMarker
    var title: CharSequence? = null

    @NotificationUtilsMarker
    var message: CharSequence? = null

    @DrawableRes
    @NotificationUtilsMarker
    var smallIconId: Int? = null

    private var privatePendingIntent: PendingIntent? = null

    @NotificationUtilsMarker
    fun pendingActivity(gotoActivity: Class<*>?, requestCode: Int = 0, block: TaskStackBuilder.() -> Unit = {}) {
        privatePendingIntent = gotoActivity?.let {
            TaskStackBuilder.create(context)
                .addParentStack(gotoActivity)
                .addNextIntent(Intent(context, it))
                .apply(block)
                .getPendingIntent(requestCode, PendingIntent.FLAG_UPDATE_CURRENT)
        }
    }

    @NotificationUtilsMarker
    fun pendingActivity(pendingIntent: PendingIntent?) = run { privatePendingIntent = pendingIntent }

    private val actions = mutableListOf<NotificationAction>()

    @NotificationUtilsMarker
    fun addReplyAction(block: NotificationAction.Reply.() -> Unit) {
        actions.add(NotificationAction.Reply(context).apply(block))
    }

    @NotificationUtilsMarker
    fun addAction(block: NotificationAction.Action.() -> Unit) {
        actions.add(NotificationAction.Action(context).apply(block))
    }

    @NotificationActionMarker
    fun replyAction(block: NotificationAction.Reply.() -> Unit) = NotificationAction.Reply(context).apply(block)

    @NotificationActionMarker
    fun actionAction(block: NotificationAction.Action.() -> Unit) = NotificationAction.Action(context).apply(block)

    operator fun NotificationAction.unaryPlus() = actions.add(this).let { Unit }

    @NotificationUtilsMarker
    var autoCancel: Boolean = false

    @NotificationUtilsMarker
    var colorized: Boolean = false

    @NotificationUtilsMarker
    var timeoutAfter: Long? = null

    @NotificationUtilsMarker
    var localOnly: Boolean = false

    @NotificationUtilsMarker
    var ongoing: Boolean = false

    @NotificationUtilsMarker
    var number: Int = 0

    @NotificationUtilsMarker
    var subText: CharSequence = ""

    @NotificationUtilsMarker
    var onlyAlertOnce: Boolean = false

    private var notificationNotificationStyle: NotificationStyle? = null

    @NotificationStyleMarker
    fun inboxStyle(block: NotificationStyle.Inbox.() -> Unit) = run { notificationNotificationStyle = NotificationStyle.Inbox().apply(block) }

    @NotificationStyleMarker
    fun pictureStyle(block: NotificationStyle.Picture.() -> Unit) = run { notificationNotificationStyle = NotificationStyle.Picture().apply(block) }

    @NotificationStyleMarker
    fun bigTextStyle(block: NotificationStyle.BigText.() -> Unit) = run { notificationNotificationStyle = NotificationStyle.BigText().apply(block) }

    @RequiresApi(Build.VERSION_CODES.O)
    @NotificationStyleMarker
    fun mediaStyle(block: NotificationStyle.Media.() -> Unit) = run { notificationNotificationStyle = NotificationStyle.Media().apply(block) }

    @NotificationStyleMarker
    fun customStyle(block: NotificationStyle?) = run { notificationNotificationStyle = block }

    private fun build() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        Notification.Builder(context, channelId)
            .whatIfNotNull(smallIconId) { setSmallIcon(it) }
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(autoCancel)
            .setColorized(colorized)
            .also { if (timeoutAfter != null) it.setTimeoutAfter(timeoutAfter!!) }
            .setLocalOnly(localOnly)
            .setOngoing(ongoing)
            .setNumber(number)
            .setSubText(subText)
            .setStyle(notificationNotificationStyle?.buildSdk())
            .setOnlyAlertOnce(onlyAlertOnce)
            .setGroup(groupId.let { if (it.isEmpty()) channelId else it })
            .setContentIntent(privatePendingIntent)
            .also { builder -> actions.forEach { builder.addAction(it.buildSdk()) } }
            .build()
    } else {
        NotificationCompat.Builder(context, channelId)
            .whatIfNotNull(smallIconId) { setSmallIcon(it) }
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(autoCancel)
            .setColorized(colorized)
            .also { if (timeoutAfter != null) it.setTimeoutAfter(timeoutAfter!!) }
            .setLocalOnly(localOnly)
            .setOngoing(ongoing)
            .setNumber(number)
            .setSubText(subText)
            .setStyle(notificationNotificationStyle?.build())
            .setOnlyAlertOnce(onlyAlertOnce)
            .setGroup(groupId.let { if (it.isEmpty()) channelId else it })
            .setContentIntent(privatePendingIntent)
            .also { builder -> actions.forEach { builder.addAction(it.build()) } }
            .build()
    }

    companion object {
        fun builder(context: Context, block: NotificationDslBuilder.() -> Unit): Notification = NotificationDslBuilder(context).apply(block).build()
    }

}

enum class SemanticActions(internal val id: Int) {
    /**
     * []: No semantic action defined.
     */
    NONE(0),

    /**
     * `SemanticAction`: Reply to a conversation, chat, group, or wherever replies
     * may be appropriate.
     */
    REPLY(1),

    /**
     * `SemanticAction`: Mark content as read.
     */
    MARK_AS_READ(2),

    /**
     * `SemanticAction`: Mark content as unread.
     */
    MARK_AS_UNREAD(3),

    /**
     * `SemanticAction`: Delete the content associated with the notification. This
     * could mean deleting an email, message, etc.
     */
    DELETE(4),

    /**
     * `SemanticAction`: Archive the content associated with the notification. This
     * could mean archiving an email, message, etc.
     */
    ARCHIVE(5),

    /**
     * `SemanticAction`: Mute the content associated with the notification. This could
     * mean silencing a conversation or currently playing media.
     */
    MUTE(6),

    /**
     * `SemanticAction`: Unmute the content associated with the notification. This could
     * mean un-silencing a conversation or currently playing media.
     */
    UNMUTE(7),

    /**
     * `SemanticAction`: Mark content with a thumbs up.
     */
    THUMBS_UP(8),

    /**
     * `SemanticAction`: Mark content with a thumbs down.
     */
    THUMBS_DOWN(9),

    /**
     * `SemanticAction`: Call a contact, group, etc.
     */
    CALL(10)

}

//Style Builder
abstract class NotificationStyle {

    /**
     * Used on versions below O
     */
    abstract fun build(): NotificationCompat.Style

    /**
     * Used on versions O and up
     */
    abstract fun buildSdk(): Notification.Style

    class Inbox : NotificationStyle() {
        private val lines = mutableListOf<CharSequence>()

        @NotificationStyleMarker
        fun addLine(vararg cs: CharSequence) = lines.addAll(cs).let { Unit }

        @NotificationStyleMarker
        var contentTitle: CharSequence = ""

        @NotificationStyleMarker
        var summaryText: CharSequence = ""

        override fun buildSdk(): Notification.Style = Notification.InboxStyle()
            .setBigContentTitle(contentTitle)
            .setSummaryText(summaryText)
            .also { builder -> lines.forEach { builder.addLine(it) } }

        override fun build(): NotificationCompat.Style = NotificationCompat.InboxStyle()
            .setBigContentTitle(contentTitle)
            .setSummaryText(summaryText)
            .also { builder -> lines.forEach { builder.addLine(it) } }

    }

    class Picture : NotificationStyle() {
        @NotificationStyleMarker
        var contentTitle: CharSequence = ""

        @NotificationStyleMarker
        var summaryText: CharSequence = ""

        @NotificationStyleMarker
        var bigPicture: Bitmap? = null

        @NotificationStyleMarker
        var largeIcon: Bitmap? = null

        override fun buildSdk(): Notification.Style = Notification.BigPictureStyle()
            .bigLargeIcon(largeIcon)
            .bigPicture(bigPicture)
            .setBigContentTitle(contentTitle)
            .setSummaryText(summaryText)

        override fun build(): NotificationCompat.Style = NotificationCompat.BigPictureStyle()
            .bigLargeIcon(largeIcon)
            .bigPicture(bigPicture)
            .setBigContentTitle(contentTitle)
            .setSummaryText(summaryText)
    }

    class BigText : NotificationStyle() {

        @NotificationStyleMarker
        var contentTitle: CharSequence = ""

        @NotificationStyleMarker
        var summaryText: CharSequence = ""

        @NotificationStyleMarker
        var bigText: CharSequence = ""

        override fun buildSdk(): Notification.Style = Notification.BigTextStyle()
            .bigText(bigText)
            .setBigContentTitle(contentTitle)
            .setSummaryText(summaryText)

        override fun build(): NotificationCompat.Style = NotificationCompat.BigTextStyle()
            .bigText(bigText)
            .setBigContentTitle(contentTitle)
            .setSummaryText(summaryText)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    class Media : NotificationStyle() {

        @NotificationStyleMarker
        var mediaSessionToken: MediaSession.Token by Delegates.notNull()

        private var actions: IntArray? = null

        @NotificationStyleMarker
        fun actions(vararg action: Int) = run { actions = action }

        override fun buildSdk(): Notification.Style = Notification.MediaStyle()
            .setMediaSession(mediaSessionToken)
            .also { if (actions != null) it.setShowActionsInCompactView(*actions!!) }

        override fun build(): NotificationCompat.Style = throw Exception("Media Style is only usable on version O and up")

    }
}

//Action Builder
sealed class NotificationAction(private val context: Context) {
    @NotificationActionMarker
    var actionTitle: CharSequence by Delegates.notNull()

    @NotificationActionMarker
    var actionIcon: Int by Delegates.notNull()

    class Reply(context: Context) : NotificationAction(context) {

        @NotificationActionMarker
        var resultKey: String by Delegates.notNull()

        @NotificationActionMarker
        var label: String by Delegates.notNull()

        @NotificationActionMarker
        var allowFreeFormInput: Boolean = true

        private val choices = mutableListOf<CharSequence>()

        operator fun CharSequence.unaryPlus() = choices.add(this).let { Unit }

        @NotificationActionMarker
        fun addChoice(s: CharSequence) = +s

        fun buildRemoteInput() = RemoteInput.Builder(resultKey)
            .setLabel(label)
            .setAllowFreeFormInput(allowFreeFormInput)
            .also { if (choices.isNotEmpty()) it.setChoices(choices.toTypedArray()) }
            .build()

        fun buildRemoteInputSdk() = android.app.RemoteInput.Builder(resultKey)
            .setLabel(label)
            .setAllowFreeFormInput(allowFreeFormInput)
            .also { if (choices.isNotEmpty()) it.setChoices(choices.toTypedArray()) }
            .build()

    }

    class Action(context: Context) : NotificationAction(context)

    /**
     * @see Notification.Action.Builder.setContextual
     */
    @NotificationActionMarker
    var contextual: Boolean = false

    /**
     * @see Notification.Action.Builder.setAllowGeneratedReplies
     */
    @NotificationActionMarker
    var allowGeneratedReplies: Boolean = true

    /**
     * @see Notification.Action.Builder.setSemanticAction
     */
    @NotificationActionMarker
    var semanticAction: SemanticActions = SemanticActions.NONE

    internal fun buildSdk() = Notification.Action.Builder(actionIcon, actionTitle, pendingIntentAction)
        .also { if (this is Reply) it.addRemoteInput(buildRemoteInputSdk()) }
        .also { if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) it.setAllowGeneratedReplies(allowGeneratedReplies) }
        .also { if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) it.setContextual(contextual) }
        .also { if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) it.setSemanticAction(semanticAction.id) }
        .build()

    internal fun build() = NotificationCompat.Action.Builder(actionIcon, actionTitle, pendingIntentAction)
        .also { if (this is Reply) it.addRemoteInput(buildRemoteInput()) }
        .setAllowGeneratedReplies(allowGeneratedReplies)
        .setContextual(contextual)
        .setSemanticAction(semanticAction.id)
        .build()

    private var pendingIntentAction: PendingIntent? = null

    @NotificationActionMarker
    fun pendingActivity(gotoActivity: Class<*>, requestCode: Int = 0, block: Intent.() -> Unit = {}) {
        pendingIntentAction = PendingIntent.getBroadcast(
            context,
            requestCode,
            Intent(context, gotoActivity).apply(block),
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    @NotificationActionMarker
    fun pendingActivity(pendingIntent: PendingIntent?) = run { pendingIntentAction = pendingIntent }

}