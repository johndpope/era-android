package com.rapidsos.era.network

/**
 * @author Josias Sena
 */
class EraApiBuilder {

    private var api: EraApi? = null

    /**
     * Builds the api with the host passed in
     *
     * @param host the URL to use as the base url for each network call made
     */
    fun buildApi(host: String, retrofitConfigurations: EraRetrofitConfigurations): EraApi {
        val retrofitInstance = retrofitConfigurations.getRetrofitInstance(host)
        api = retrofitInstance.create(EraApi::class.java)
        return api as EraApi
    }


}