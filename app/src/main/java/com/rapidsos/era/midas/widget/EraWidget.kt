package com.rapidsos.era.midas.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.rapidsos.era.R
import com.rapidsos.shared.PanicInitiator

/**
 * The ERA home screen widget that allows the user to trigger an emergency from their
 * home screen without having to open up any applicationø
 *
 * @author Josias Sena
 */
class EraWidget : AppWidgetProvider() {

    /**
     * Called in response to the [AppWidgetManager.ACTION_APPWIDGET_UPDATE] and
     * [AppWidgetManager.ACTION_APPWIDGET_RESTORED] broadcasts when this AppWidget
     * provider is being asked to provide {@link android.widget.RemoteViews RemoteViews}
     * for a set of AppWidgets.  Override this method to implement your own AppWidget
     * functionality.
     *
     * @param context The [Context] in which this receiver is running.
     * @param appWidgetManager A {@link AppWidgetManager} object you can call
     * [AppWidgetManager.updateAppWidget] on.
     * @param appWidgetIds The appWidgetIds for which an update is needed.
     * Note that this may be all of the AppWidget instances for this provider, or just a subset
     * of them.
     *
     * @see AppWidgetManager.ACTION_APPWIDGET_UPDATE
     */
    override fun onUpdate(context: Context,
                          appWidgetManager: AppWidgetManager,
                          appWidgetIds: IntArray) = appWidgetIds.forEach { widgetId ->
        val remoteViews = setupWidgetRemoteViews(context, widgetId)
        appWidgetManager.updateAppWidget(widgetId, remoteViews)
    }

    /**
     * Create the views for the widget
     */
    private fun setupWidgetRemoteViews(context: Context, widgetId: Int): RemoteViews {
        val panicManager = PanicInitiator(context)
        val widgetPendingIntent = panicManager.getWidgetPendingIntent(widgetId)

        return RemoteViews(context.packageName, R.layout.era_widget).apply {
            setOnClickPendingIntent(R.id.widget_body, widgetPendingIntent)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        val thisWidget = ComponentName(context, EraWidget::class.java)
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val widgetIds = appWidgetManager.getAppWidgetIds(thisWidget)

        widgetIds.map { setupWidgetRemoteViews(context, it) }
                .forEach { appWidgetManager.updateAppWidget(thisWidget, it) }
    }

}