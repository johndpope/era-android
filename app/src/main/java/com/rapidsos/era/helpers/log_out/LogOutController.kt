package com.rapidsos.era.helpers.log_out

import android.content.Context
import com.rapidsos.database.database.DatabaseCleaner
import com.rapidsos.era.authentication.auth_activity.view.AuthenticationActivity
import com.rapidsos.shared.notification.PanicNotificationHandler
import com.rapidsos.utils.preferences.EraPreferences
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask
import javax.inject.Inject

/**
 * Handles logging the user out of the application
 *
 * @see DatabaseCleaner
 * @see EraPreferences
 * @see PanicNotificationHandler
 *
 * @author Josias Sena
 */
class LogOutController @Inject constructor(private val context: Context,
                                           private val databaseCleaner: DatabaseCleaner,
                                           private val panicNotificationHandler: PanicNotificationHandler) {

    private val preferences: EraPreferences by lazy(LazyThreadSafetyMode.PUBLICATION) {
        EraPreferences(context)
    }

    /**
     * Logs the user out. Clears all preferences, all of the data in the database, and
     * dismisses notifications.
     *
     * Once all data has been cleared, the user is taken to the [AuthenticationActivity]
     */
    fun logOut() {
        databaseCleaner.deleteAllTables()
        preferences.clearAllPreferences()
        panicNotificationHandler.dismissNotification()

        context.startActivity(context.intentFor<AuthenticationActivity>()
                .clearTask().newTask())
    }

}