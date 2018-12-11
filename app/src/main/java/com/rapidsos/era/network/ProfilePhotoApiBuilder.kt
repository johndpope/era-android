package com.rapidsos.era.network

/**
 * @author Josias Sena
 */
class ProfilePhotoApiBuilder {

    private var profilePhotoApi: ProfilePhotoApi? = null

    /**
     * Builds the profilePhotoApi with the host passed in
     *
     * @param host the URL to use as the base url for each network call made
     */
    fun buildApi(host: String, retrofitConfigurations: EraRetrofitConfigurations): ProfilePhotoApi {
        val retrofitInstance = retrofitConfigurations.getRetrofitInstance(host)
        profilePhotoApi = retrofitInstance.create(ProfilePhotoApi::class.java)
        return profilePhotoApi as ProfilePhotoApi
    }


}