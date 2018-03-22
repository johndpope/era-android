package com.josiassena.bluetooth.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.os.Build
import android.support.v4.app.NotificationCompat
import com.rapidsos.shared.R
import org.jetbrains.anko.AnkoLogger

/**
 * @author Josias Sena
 */
class BtConnectedNotification(private val context: Context) : AnkoLogger {

    private val notificationManager: NotificationManager by lazy(LazyThreadSafetyMode.PUBLICATION) {
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    private lateinit var notificationCompat: Notification

    companion object {
        private const val NOTIFICATION_TAG = "btNotification"
        private const val CHANNEL_ID = "bt_notification_channel"

        const val NOTIFICATION_ID = 17847
    }

    init {
        buildNotificationChannel()
    }

    /**
     * Builds the notification channel for the ERA ongoing notification.
     * Required for android O(api 26) and above.
     */
    private fun buildNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, "ERA", importance).apply {
                description = "Connected to bluetooth device"
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }

            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * Build a notification which informs the user of being connected to a bluetooth device
     *
     * @param device the device connected to
     * @param actionIntent intent launched when the notification is clicked
     */
    fun buildNotification(device: BluetoothDevice, actionIntent: PendingIntent): Notification {
        val deviceName = device.name
        val deviceMacAddress = device.address

        notificationCompat = NotificationCompat.Builder(context, CHANNEL_ID).apply {
            setSmallIcon(R.drawable.ic_notification)
            setContentTitle(context.getString(R.string.app_name))
            setTicker("You are connected to a bluetooth device.")
            setContentText("Connected to: $deviceName ($deviceMacAddress)")
            setOngoing(true)
            setChannelId(CHANNEL_ID)

            val notificationStyle = NotificationCompat.BigTextStyle()
                    .bigText("Connected to: $deviceName ($deviceMacAddress)")

            setStyle(notificationStyle)

            priority = NotificationCompat.PRIORITY_MAX

            setVisibility(Notification.VISIBILITY_PUBLIC)

            setContentIntent(actionIntent)
        }.build()

        return notificationCompat
    }

    /**
     * Dismiss the lock screen on going notification
     */
    fun dismissNotification() {
        notificationManager.cancel(NOTIFICATION_TAG, NOTIFICATION_ID)
    }
}