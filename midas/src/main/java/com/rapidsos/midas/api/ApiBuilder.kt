package com.rapidsos.midas.api

/**
 * @author Josias Sena
 */
object ApiBuilder {

    private var api: MidasApi? = null

    /**
     * Builds the api with the host passed in
     *
     * @param host the URL to use as the base url for each network call made
     */
    fun buildApi(host: String, retrofitConfigurations: MidasRetrofitConfigurations): MidasApi {
        val retrofitInstance = retrofitConfigurations.getRetrofitInstance(host)
        api = retrofitInstance.create(MidasApi::class.java)
        return api as MidasApi
    }

}