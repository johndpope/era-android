package com.rapidsos.midas.api

import com.rapidsos.midas.data.Oauth
import com.rapidsos.midas.data.Trigger
import com.rapidsos.midas.helpers.AuthCredentials
import com.rapidsos.midas.helpers.MIDAS_HOST
import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

/**
 * @author Josias Sena
 */
interface MidasApi {

    /**
     * Get an access token to use when triggering the midas flow.
     */
    @FormUrlEncoded
    @POST
    fun getAccessToken(@Url amlEndpoint: String = MIDAS_HOST + "oauth/token",
                       @Header("Authorization") credentials: String = AuthCredentials.getBasicCredentials(),
                       @Field(value = "grant_type", encoded = true) grantType: String = "client_credentials"):
            Single<Response<Oauth>>

    /**
     * Trigger a midas flow.
     */
    @POST("v1/rem/trigger")
    fun triggerFlow(@Header("Authorization") bearerAccessToken: String,
                    @Body trigger: Trigger): Single<Response<ResponseBody>>

}