package com.programmersbox.helpfulutils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationChannelGroup
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Icon
import android.media.session.MediaSession
import android.os.Build
import android.os.Bundle
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

    /**
     * @see Notification.Builder.setChannelId
     */
    @NotificationUtilsMarker
    var channelId: String by Delegates.notNull()

    /**
     * @see Notification.Builder.setGroup
     */
    @NotificationUtilsMarker
    var groupId: String = ""

    /**
     * @see Notification.Builder.setContentTitle
     */
    @NotificationUtilsMarker
    var title: CharSequence? = null

    /**
     * @see Notification.Builder.setContentText
     */
    @NotificationUtilsMarker
    var message: CharSequence? = null

    /**
     * @see Notification.Builder.setSmallIcon
     */
    @NotificationUtilsMarker
    var smallIconId: Int by Delegates.notNull()

    /**
     * @see Notification.Builder.setLargeIcon
     */
    @NotificationUtilsMarker
    var largeIconBitmap: Bitmap? = null

    /**
     * @see Notification.Builder.setLargeIcon
     */
    @NotificationUtilsMarker
    var largeIconIcon: Icon? = null

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

    /**
     * @see Notification.Builder.setContentIntent
     */
    @NotificationUtilsMarker
    fun pendingActivity(pendingIntent: PendingIntent?) = run { privatePendingIntent = pendingIntent }

    private var privateDeleteIntent: PendingIntent? = null

    /**
     * @see Notification.Builder.setDeleteIntent
     */
    @NotificationUtilsMarker
    fun deleteIntent(pendingIntent: PendingIntent?) = run { privateDeleteIntent = pendingIntent }

    private val actions = mutableListOf<NotificationAction>()

    /**
     * @see Notification.Builder.setActions
     */
    @NotificationUtilsMarker
    fun addReplyAction(block: NotificationAction.Reply.() -> Unit) {
        actions.add(NotificationAction.Reply(context).apply(block))
    }

    /**
     * @see Notification.Builder.setActions
     */
    @NotificationUtilsMarker
    fun addAction(block: NotificationAction.Action.() -> Unit) {
        actions.add(NotificationAction.Action(context).apply(block))
    }

    /**
     * Creates a Reply Action
     */
    @NotificationActionMarker
    fun replyAction(block: NotificationAction.Reply.() -> Unit) = NotificationAction.Reply(context).apply(block)

    /**
     * Creates a Normal Action
     */
    @NotificationActionMarker
    fun actionAction(block: NotificationAction.Action.() -> Unit) = NotificationAction.Action(context).apply(block)

    operator fun NotificationAction.unaryPlus() = actions.add(this).let { Unit }

    /**
     * @see Notification.Builder.setAutoCancel
     */
    @NotificationUtilsMarker
    var autoCancel: Boolean = false

    /**
     * @see Notification.Builder.setContentIntent
     */
    @NotificationUtilsMarker
    var colorized: Boolean = false

    /**
     * @see Notification.Builder.setTimeoutAfter
     */
    @NotificationUtilsMarker
    var timeoutAfter: Long? = null

    /**
     * @see Notification.Builder.setLocalOnly
     */
    @NotificationUtilsMarker
    var localOnly: Boolean = false

    /**
     * @see Notification.Builder.setOngoing
     */
    @NotificationUtilsMarker
    var ongoing: Boolean = false

    /**
     * @see Notification.Builder.setNumber
     */
    @NotificationUtilsMarker
    var number: Int = 0

    /**
     * @see Notification.Builder.setSubText
     */
    @NotificationUtilsMarker
    var subText: CharSequence = ""

    /**
     * @see Notification.Builder.setOnlyAlertOnce
     */
    @NotificationUtilsMarker
    var onlyAlertOnce: Boolean = false

    private var extrasSet: Bundle.() -> Unit = {}

    /**
     * Add some extras to the notification
     */
    @NotificationUtilsMarker
    fun extras(block: Bundle.() -> Unit) = run { extrasSet = block }

    private var notificationNotificationStyle: NotificationStyle? = null

    /**
     * @see Notification.InboxStyle
     */
    @NotificationStyleMarker
    fun inboxStyle(block: NotificationStyle.Inbox.() -> Unit) = run { notificationNotificationStyle = NotificationStyle.Inbox().apply(block) }

    /**
     * @see Notification.BigPictureStyle
     */
    @NotificationStyleMarker
    fun pictureStyle(block: NotificationStyle.Picture.() -> Unit) = run { notificationNotificationStyle = NotificationStyle.Picture().apply(block) }

    /**
     * @see Notification.BigTextStyle
     */
    @NotificationStyleMarker
    fun bigTextStyle(block: NotificationStyle.BigText.() -> Unit) = run { notificationNotificationStyle = NotificationStyle.BigText().apply(block) }

    /**
     * @see Notification.MediaStyle
     */
    @RequiresApi(Build.VERSION_CODES.O)
    @NotificationStyleMarker
    fun mediaStyle(block: NotificationStyle.Media.() -> Unit) = run { notificationNotificationStyle = NotificationStyle.Media().apply(block) }

    /**
     * Add a custom style
     */
    @NotificationStyleMarker
    fun customStyle(block: NotificationStyle?) = run { notificationNotificationStyle = block }

    private fun build() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        Notification.Builder(context, channelId)
            .setSmallIcon(smallIconId)
            .also { builder -> largeIconBitmap?.let { builder.setLargeIcon(it) } ?: largeIconIcon?.let { builder.setLargeIcon(it) } }
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(autoCancel)
            .setColorized(colorized)
            .also { builder -> timeoutAfter?.let { builder.setTimeoutAfter(it) } }
            .setLocalOnly(localOnly)
            .setOngoing(ongoing)
            .setNumber(number)
            .also { it.extras.extrasSet() }
            .setSubText(subText)
            .setStyle(notificationNotificationStyle?.buildSdk())
            .setOnlyAlertOnce(onlyAlertOnce)
            .setGroup(groupId.let { if (it.isEmpty()) channelId else it })
            .setDeleteIntent(privateDeleteIntent)
            .setContentIntent(privatePendingIntent)
            .also { builder -> actions.forEach { builder.addAction(it.buildSdk()) } }
            .build()
    } else {
        NotificationCompat.Builder(context, channelId)
            .setSmallIcon(smallIconId)
            .also { builder -> largeIconBitmap?.let { builder.setLargeIcon(it) } }
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(autoCancel)
            .setColorized(colorized)
            .also { builder -> timeoutAfter?.let { builder.setTimeoutAfter(it) } }
            .setLocalOnly(localOnly)
            .setOngoing(ongoing)
            .setNumber(number)
            .also { it.extras.extrasSet() }
            .setSubText(subText)
            .setStyle(notificationNotificationStyle?.build())
            .setOnlyAlertOnce(onlyAlertOnce)
            .setGroup(groupId.let { if (it.isEmpty()) channelId else it })
            .setDeleteIntent(privateDeleteIntent)
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

        /**
         * @see Notification.InboxStyle.addLine
         */
        @NotificationStyleMarker
        fun addLine(vararg cs: CharSequence) = lines.addAll(cs).let { Unit }

        /**
         * @see Notification.InboxStyle.setBigContentTitle
         */
        @NotificationStyleMarker
        var contentTitle: CharSequence = ""

        /**
         * @see Notification.InboxStyle.setSummaryText
         */
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
        /**
         * @see Notification.BigPictureStyle.setBigContentTitle
         */
        @NotificationStyleMarker
        var contentTitle: CharSequence = ""

        /**
         * @see Notification.BigPictureStyle.setSummaryText
         */
        @NotificationStyleMarker
        var summaryText: CharSequence = ""

        /**
         * @see Notification.BigPictureStyle.bigPicture
         */
        @NotificationStyleMarker
        var bigPicture: Bitmap? = null

        /**
         * @see Notification.BigPictureStyle.bigLargeIcon
         */
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

        /**
         * @see Notification.BigTextStyle.setBigContentTitle
         */
        @NotificationStyleMarker
        var contentTitle: CharSequence = ""

        /**
         * @see Notification.BigTextStyle.setSummaryText
         */
        @NotificationStyleMarker
        var summaryText: CharSequence = ""

        /**
         * @see Notification.BigTextStyle.bigText
         */
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

        /**
         * @see Notification.MediaStyle.setMediaSession
         */
        @NotificationStyleMarker
        var mediaSessionToken: MediaSession.Token by Delegates.notNull()

        private var actions: IntArray? = null

        /**
         * @see Notification.MediaStyle.setShowActionsInCompactView
         */
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

    /**
     * @see Notification.Action.title
     */
    @NotificationActionMarker
    var actionTitle: CharSequence by Delegates.notNull()

    /**
     * @see Notification.Action.icon
     */
    @NotificationActionMarker
    var actionIcon: Int by Delegates.notNull()

    class Reply(context: Context) : NotificationAction(context) {

        /**
         * @see RemoteInput.Builder.mResultKey
         */
        @NotificationActionMarker
        var resultKey: String by Delegates.notNull()

        /**
         * @see RemoteInput.Builder.setLabel
         */
        @NotificationActionMarker
        var label: String by Delegates.notNull()

        /**
         * @see RemoteInput.Builder.setAllowFreeFormInput
         */
        @NotificationActionMarker
        var allowFreeFormInput: Boolean = true

        private val choices = mutableListOf<CharSequence>()

        operator fun CharSequence.unaryPlus() = choices.add(this).let { Unit }

        /**
         * @see RemoteInput.Builder.setChoices
         */
        @NotificationActionMarker
        fun addChoice(s: CharSequence) = +s

        internal fun buildRemoteInput() = RemoteInput.Builder(resultKey)
            .setLabel(label)
            .setAllowFreeFormInput(allowFreeFormInput)
            .also { if (choices.isNotEmpty()) it.setChoices(choices.toTypedArray()) }
            .build()

        internal fun buildRemoteInputSdk() = android.app.RemoteInput.Builder(resultKey)
            .setLabel(label)
            .setAllowFreeFormInput(allowFreeFormInput)
            .also { if (choices.isNotEmpty()) it.setChoices(choices.toTypedArray()) }
            .build()

    }

    class Action(context: Context) : NotificationAction(context)

    /**
     * @see Notification.Action.Builder.setContextual
     */
    @RequiresApi(Build.VERSION_CODES.Q)
    @NotificationActionMarker
    var contextual: Boolean = false

    /**
     * @see Notification.Action.Builder.setAllowGeneratedReplies
     */
    @RequiresApi(Build.VERSION_CODES.N)
    @NotificationActionMarker
    var allowGeneratedReplies: Boolean = true

    /**
     * @see Notification.Action.Builder.setSemanticAction
     */
    @RequiresApi(Build.VERSION_CODES.P)
    @NotificationActionMarker
    var semanticAction: SemanticActions = SemanticActions.NONE

    internal fun buildSdk() = Notification.Action.Builder(Icon.createWithResource(context, actionIcon), actionTitle, pendingIntentAction)
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