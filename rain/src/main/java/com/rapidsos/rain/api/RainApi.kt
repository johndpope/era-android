package com.rapidsos.rain.api

import com.google.gson.JsonObject
import com.rapidsos.rain.data.network_response.CallerId
import com.rapidsos.rain.data.network_response.GetProfileUrlResponse
import com.rapidsos.rain.data.network_response.OauthResponse
import com.rapidsos.rain.data.network_response.SessionToken
import com.rapidsos.rain.data.profile.Profile
import com.rapidsos.rain.data.user.User
import com.rapidsos.rain.helpers.CLIENT_ID
import com.rapidsos.rain.helpers.CLIENT_SECRET
import io.reactivex.Observable
import io.reactivex.Single
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

/**
 * @author Josias Sena
 */
interface RainApi {

    @FormUrlEncoded
    @POST("oauth/token")
    fun getAccessToken(@Header("Authorization") credentials: String,
                       @Field(value = "grant_type", encoded = true) grantType: String = "client_credentials"):
            Single<Response<OauthResponse>>

    @FormUrlEncoded
    @POST("oauth/token")
    fun login(@Field(value = "grant_type", encoded = true) grantType: String = "password",
              @Field(value = "client_id", encoded = true) clientId: String = CLIENT_ID,
              @Field(value = "client_secret", encoded = true) clientSecret: String = CLIENT_SECRET,
              @Field(value = "username", encoded = true) username: String,
              @Field(value = "password", encoded = true) password: String):
            Observable<Response<SessionToken>>

    @POST("v1/rain/user")
    fun register(@Header("Authorization") accessToken: String,
                 @Body user: JsonObject): Observable<Response<User>>

    @POST("v1/rain/caller-ids")
    fun createCallerId(@Header("Authorization") accessToken: String,
                       @Body user: JsonObject): Single<Response<CallerId>>

    @PATCH("v1/rain/caller-ids")
    fun validateCallerId(@Header("Authorization") accessToken: String,
                         @Body user: JsonObject): Single<Response<CallerId>>

    @GET("v1/rain/personal-info")
    fun getPersonalInfo(@Header("Authorization") accessToken: String): Single<Response<Profile>>

    @PATCH("v1/rain/personal-info")
    fun updatePersonalInfo(@Header("Authorization") accessToken: String, @Body profile: Profile):
            Single<Response<Profile>>

    @GET("/v1/kronos/upload-url")
    fun getProfilePictureUploadUrl(@Query("key") fileName: String): Single<Response<GetProfileUrlResponse>>

    @Multipart
    @POST
    fun uploadPic(@Url uploadUrl: String,
                  @Part("AWSAccessKeyId") awsAccessKeyId: RequestBody,
                  @Part("key") key: RequestBody,
                  @Part("policy") policy: RequestBody,
                  @Part("signature") signature: RequestBody,
                  @Part("x-amz-security-token") xAmzSecurityToken: RequestBody,
                  @Part file: MultipartBody.Part): Single<Response<ResponseBody>>

    @POST("v1/rain/password-reset")
    fun resetPassword(@Header("Authorization") accessToken: String,
                      @Body email: JsonObject): Single<Response<ResponseBody>>
}