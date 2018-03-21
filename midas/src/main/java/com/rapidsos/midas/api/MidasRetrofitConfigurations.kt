package com.rapidsos.midas.api

import android.content.Context
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Handles all retrofit configurations for the [MidasApi]
 *
 * @author Josias Sena
 */
class MidasRetrofitConfigurations(private val context: Context) {

    /**
     * Builds the api with the host passed in
     *
     * @param host the URL to use as the base url for each network call made
     * @return a [Retrofit] instance built using the host as the base url
     */
    fun getRetrofitInstance(host: String): Retrofit = Retrofit.Builder()
            .baseUrl(host)
            .addConverterFactory(getGSONConverterFactory())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(getOkHttpClient())
            .build()

    private fun getGSONConverterFactory() = GsonConverterFactory.create(GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create())

    private fun getOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
            .cache(getCache(context))
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .build()

    private fun getCache(context: Context) = Cache(context.cacheDir, (12 * 1024 * 1024).toLong())

}