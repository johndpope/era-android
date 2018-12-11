package com.rapidsos.era.helpers.dependency_injection.modules

import android.content.Context
import com.rapidsos.beaconsdk.BeaconPreferences
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * @author Josias Sena
 */
@Module
class BeaconPreferencesModule(private val context: Context) {

    @Provides
    @Singleton
    fun providesBeaconPreferences() = BeaconPreferences(context)

}