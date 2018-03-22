package com.rapidsos.era.helpers.dependency_injection.modules

import android.content.Context
import com.rapidsos.androidutils.PermissionUtils
import com.rapidsos.androidutils.phone_book.PhoneBookManager
import com.rapidsos.utils.utils.Utils
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * @author Josias Sena
 */
@Module
class UtilsModule(private val context: Context) {

    @Provides
    @Singleton
    fun providesUtils() = Utils(context)

    @Provides
    @Singleton
    fun providesPermissionUtils() = PermissionUtils(context)

    @Provides
    @Singleton
    fun providesPhoneBookManager() = PhoneBookManager(context)

}