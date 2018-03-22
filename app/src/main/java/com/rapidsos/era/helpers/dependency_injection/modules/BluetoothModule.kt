package com.rapidsos.era.helpers.dependency_injection.modules

import android.content.Context
import com.josiassena.bluetooth.BluetoothHandler
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * @author Josias Sena
 */
@Module
class BluetoothModule(private val context: Context) {

    @Provides
    @Singleton
    fun providesVSNBluetoothManager() = BluetoothHandler(context)

}