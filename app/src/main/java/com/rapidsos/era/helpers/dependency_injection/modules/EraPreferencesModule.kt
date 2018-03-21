package com.rapidsos.era.helpers.dependency_injection.modules

import android.content.Context
import com.rapidsos.utils.preferences.EraPreferences
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * @author Josias Sena
 */
@Module
class EraPreferencesModule(private val context: Context) {

    @Provides
    @Singleton
    fun providesEraPreferences() = EraPreferences(context)

}