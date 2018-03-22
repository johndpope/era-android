package com.rapidsos.era.helpers.dependency_injection.modules

import android.app.NotificationManager
import android.content.Context
import com.rapidsos.shared.PanicInitiator
import dagger.Module
import dagger.Provides
import pl.charmas.android.reactivelocation2.ReactiveLocationProvider
import javax.inject.Singleton

/**
 * @author Josias Sena
 */
@Module
class AndroidModule(private val context: Context) {

    @Provides
    @Singleton
    fun providesNotificationManager() = context.getSystemService(Context.NOTIFICATION_SERVICE)
            as NotificationManager

    @Provides
    @Singleton
    fun providesReactiveLocationProvider() = ReactiveLocationProvider(context)

    @Provides
    @Singleton
    fun providesPanicManager() = PanicInitiator(context)

}