package com.rapidsos.shared

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent

/**
 * Handles initiating a panic.
 *
 * @author Josias Sena
 */
class PanicInitiator(private val context: Context) {

    private val instantPanicIntent: PendingIntent by lazy(LazyThreadSafetyMode.PUBLICATION) {
        val panicIntent = Intent(context, PanicIntentService::class.java).apply {
            action = PanicIntentService.ACTION_INSTANT_PANIC
        }

        PendingIntent.getService(context, 0, panicIntent, 0)
    }

    val confirmationPanicIntent: PendingIntent by lazy(LazyThreadSafetyMode.PUBLICATION) {
        val panicIntent = Intent(context, PanicIntentService::class.java).apply {
            action = PanicIntentService.ACTION_PANIC_WITH_CONFIRMATION
        }

        PendingIntent.getService(context, 0, panicIntent, 0)
    }

    /**
     * Panic instantly. With no confirmation view
     */
    fun panic() = instantPanicIntent.send()

    /**
     * Display a dialog to the user for confirmation before initiating a panic
     */
    fun panicWithConfirmation() = confirmationPanicIntent.send()

    /**
     * Build a pending messageIntent for a widget with a panic with confirmation action
     *
     * @param widgetId the widgetId who is requesting this intent
     */
    fun getWidgetPendingIntent(widgetId: Int): PendingIntent {
        val intent = Intent(context, PanicIntentService::class.java).apply {
            this.action = PanicIntentService.ACTION_PANIC_WITH_CONFIRMATION
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
        }

        return PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

}