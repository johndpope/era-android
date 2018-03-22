package com.rapidsos.era.helpers.dependency_injection.modules

import android.content.Context
import com.rapidsos.era.helpers.profile_photo.ProfilePhotoHandler
import com.rapidsos.era.network.EraRetrofitConfigurations
import com.rapidsos.era.network.ProfilePhotoApi
import com.rapidsos.era.network.ProfilePhotoApiBuilder
import dagger.Module
import dagger.Provides
import okhttp3.Cache
import javax.inject.Singleton

/**
 * @author Josias Sena
 */
@Module
class ProfilePhotoModule(private val context: Context) {

    @Provides
    @Singleton
    fun providesProfilePhotoApi(): ProfilePhotoApi {
        val cache = Cache(context.cacheDir, (12 * 1024 * 1024).toLong())
        return ProfilePhotoApiBuilder().buildApi("https://api-dev.rapidsos.com/", EraRetrofitConfigurations(cache))
    }

    @Provides
    @Singleton
    fun providesProfilePhotoHandler(api: ProfilePhotoApi) = ProfilePhotoHandler(api)

}