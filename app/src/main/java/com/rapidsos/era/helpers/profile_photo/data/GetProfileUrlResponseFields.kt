package com.rapidsos.rain.data.network_response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * @author Josias Sena
 */
data class GetProfileUrlResponseFields(@SerializedName("AWSAccessKeyId")
                                       @Expose
                                       var awsAccessKeyId: String = "",

                                       @SerializedName("key")
                                       @Expose
                                       var key: String = "",

                                       @SerializedName("policy")
                                       @Expose
                                       var policy: String = "",

                                       @SerializedName("signature")
                                       @Expose
                                       var signature: String = "",

                                       @SerializedName("x-amz-security-token")
                                       @Expose
                                       var xAmzSecurityToken: String = "")