package com.rapidsos.era.helpers.dependency_injection.modules

import android.content.Context
import com.rapidsos.midas.api.ApiBuilder
import com.rapidsos.midas.api.MidasRetrofitConfigurations
import com.rapidsos.midas.flow.MidasFLow
import com.rapidsos.midas.helpers.MIDAS_HOST
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * @author Josias Sena
 */
@Module
class MidasModule(private val context: Context) {

    @Provides
    @Singleton
    fun providesMidasRetrofitConfigurations() = MidasRetrofitConfigurations(context)

    @Provides
    @Singleton
    fun providesMidasApi(configurationsMidas: MidasRetrofitConfigurations) =
            ApiBuilder.buildApi(MIDAS_HOST, configurationsMidas)

    @Provides
    @Singleton
    fun providesTriggerManager() = MidasFLow(context)

}