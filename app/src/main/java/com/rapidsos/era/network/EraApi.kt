package com.rapidsos.era.network

import com.rapidsos.rain.data.network_response.GetProfileUrlResponse
import io.reactivex.Single
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

/**
 * @author Josias Sena
 */
interface EraApi {

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

}