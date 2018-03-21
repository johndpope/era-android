package com.rapidsos.rain.api

/**
 * @author Josias Sena
 */
class ApiBuilder(private val configurations: RetrofitConfigurations) {

    /**
     * Builds the api with the host passed in
     *
     * @param host the URL to use as the base url for each network call made
     */
    fun buildApi(host: String): RainApi {
        val retrofitInstance = configurations.getRetrofitInstance(host)
        return retrofitInstance.create(RainApi::class.java)
    }

}