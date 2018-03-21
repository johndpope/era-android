package com.rapidsos.era.helpers.dependency_injection.modules

import android.content.Context
import com.rapidsos.shared.notification.PanicNotificationHandler
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * @author Josias Sena
 */
@Module
class NotificationModule(private val context: Context) {

    @Provides
    @Singleton
    fun providesKNotificationManager() = PanicNotificationHandler(context)

}