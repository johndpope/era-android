package com.rapidsos.shared.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.support.v4.app.NotificationCompat
import com.rapidsos.shared.PanicInitiator
import com.rapidsos.shared.R
import com.rapidsos.utils.preferences.EraPreferences

/**
 * @author josiassena
 */
class PanicNotificationHandler(private val context: Context) {

    private val preferences: EraPreferences by lazy(LazyThreadSafetyMode.PUBLICATION) {
        EraPreferences(context)
    }

    private val notificationManager: NotificationManager by lazy(LazyThreadSafetyMode.PUBLICATION) {
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    private val panicInitiator: PanicInitiator by lazy(LazyThreadSafetyMode.PUBLICATION) {
        PanicInitiator(context)
    }

    private lateinit var notificationCompat: Notification

    companion object {
        private const val NOTIFICATION_TAG = "PanicNotificationHandler"
        private const val CHANNEL_ID = "era_notification_channel"
        private const val WEARABLE_BRIDGE_TAG = "ERA Wearable Notification"
        private const val WEARABLE_DISMISSAL_ID = "ERA Wearable Notification Dismissal Id"

        const val NOTIFICATION_ID = 185
        const val NOTIFICATION_PANIC_ID = 856
    }

    init {
        buildNotificationChannel()

        buildPanicNotification()
    }

    /**
     * Build and display a normal notification with a custom body message
     *
     * @param messageBody the string to use as the notifications message body
     */
    fun showNotification(messageBody: String) {
        notificationCompat = NotificationCompat.Builder(context, CHANNEL_ID).apply {
            setSmallIcon(R.drawable.ic_notification)
            setContentTitle(context.getString(R.string.app_name))
            setContentText(messageBody)
            setTicker(messageBody)
            setChannelId(CHANNEL_ID)

            setDefaults(Notification.DEFAULT_ALL)

            val notificationStyle = NotificationCompat.BigTextStyle()
                    .bigText(messageBody)

            setStyle(notificationStyle)

            extend(getWearableExtender())

            priority = NotificationCompat.PRIORITY_MAX

            setVisibility(Notification.VISIBILITY_PUBLIC)
        }.build()

        notificationManager.notify(NOTIFICATION_TAG, NOTIFICATION_ID, notificationCompat)
    }

    /**
     * Builds the notification to be displayed when [displayNotification] is called
     */
    private fun buildPanicNotification() {
        notificationCompat = NotificationCompat.Builder(context, CHANNEL_ID).apply {
            val messageBody = "Once the alert has been initiated, the alert cannot be canceled."

            setSmallIcon(R.drawable.ic_notification)
            setContentTitle(context.getString(R.string.app_name))
            setContentText(messageBody)
            setTicker(messageBody)
            setOngoing(true)
            setChannelId(CHANNEL_ID)

            val notificationStyle = NotificationCompat.BigTextStyle().bigText(messageBody)
            setStyle(notificationStyle)

            extend(getWearableExtender())

            priority = NotificationCompat.PRIORITY_MAX

            setVisibility(Notification.VISIBILITY_PUBLIC)

            val pendingIntent = panicInitiator.confirmationPanicIntent
            addAction(R.drawable.ic_notifications_active_black, "Panic", pendingIntent)

        }.build()
    }

    private fun getWearableExtender(): NotificationCompat.WearableExtender {
        return NotificationCompat.WearableExtender().apply {
            bridgeTag = WEARABLE_BRIDGE_TAG
            dismissalId = WEARABLE_DISMISSAL_ID
            this.contentIcon = R.mipmap.ic_launcher
        }
    }

    /**
     * Builds the notification channel for the ERA ongoing notification.
     * Required for android O(api 26) and above.
     */
    private fun buildNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, "ERA", importance).apply {
                description = "ERA panicWithConfirmation notification"
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }

            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * Display the notification
     */
    fun displayNotification() {
        notificationManager.notify(NOTIFICATION_TAG, NOTIFICATION_PANIC_ID, notificationCompat)
        preferences.setEnableLockScreenWidget(true)
    }

    /**
     * Dismiss the lock screen on going notification
     */
    fun dismissNotification() {
        notificationManager.cancel(NOTIFICATION_TAG, NOTIFICATION_PANIC_ID)
        preferences.setEnableLockScreenWidget(false)
    }
}