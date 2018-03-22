package com.rapidsos.era.network

import com.rapidsos.era.helpers.profile_photo.ProfilePhotoHandler
import com.rapidsos.rain.data.network_response.GetProfileUrlResponse
import io.reactivex.Single
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

/**
 * ```
 * __          __     _____  _   _ _____ _   _  _____ _
 * \ \        / /\   |  __ \| \ | |_   _| \ | |/ ____| |
 *  \ \  /\  / /  \  | |__) |  \| | | | |  \| | |  __| |
 *   \ \/  \/ / /\ \ |  _  /| . ` | | | | . ` | | |_ | |
 *    \  /\  / ____ \| | \ \| |\  |_| |_| |\  | |__| |_|
 *     \/  \/_/    \_\_|  \_\_| \_|_____|_| \_|\_____(_)
 *```
 * This api interface is for internal use ONLY. This is an example implementation of how photo
 * upload would be done. DO NOT use this api in your own projects. This api may be deleted/replaced/changed
 * without notice at any time.
 *
 * @author Josias Sena
 * @see ProfilePhotoHandler
 */
interface ProfilePhotoApi {

    @GET("/v1/kronos/upload-url")
    fun getProfilePhotoUploadUrl(@Query("key") fileName: String): Single<Response<GetProfileUrlResponse>>

    @Multipart
    @POST
    fun uploadPhoto(@Url uploadUrl: String,
                    @Part("AWSAccessKeyId") awsAccessKeyId: RequestBody,
                    @Part("key") key: RequestBody,
                    @Part("policy") policy: RequestBody,
                    @Part("signature") signature: RequestBody,
                    @Part("x-amz-security-token") xAmzSecurityToken: RequestBody,
                    @Part file: MultipartBody.Part): Single<Response<ResponseBody>>

}