package com.rapidsos.rain.api

import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * @author Josias Sena
 */
open class RetrofitConfigurations(private val cache: Cache?) {

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
            .setPrettyPrinting()
            .create())

    fun getOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
                .cache(cache)
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build()
    }
}