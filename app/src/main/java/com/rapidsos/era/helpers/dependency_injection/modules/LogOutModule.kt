package com.rapidsos.era.helpers.dependency_injection.modules

import android.content.Context
import com.rapidsos.database.database.DatabaseCleaner
import com.rapidsos.era.helpers.log_out.LogOutController
import com.rapidsos.era.helpers.log_out.LogOutDialog
import com.rapidsos.shared.notification.PanicNotificationHandler
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * @author Josias Sena
 */
@Module
class LogOutModule(private val context: Context) {

    @Provides
    @Singleton
    internal fun providesLogOutController(databaseCleaner: DatabaseCleaner,
                                          panicNotificationHandler: PanicNotificationHandler) =
            LogOutController(context, databaseCleaner, panicNotificationHandler)

    @Provides
    @Singleton
    internal fun providesLogOutDialog() = LogOutDialog(context)
}