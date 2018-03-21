package com.rapidsos.rain.data.network_response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class GetProfileUrlResponse(@SerializedName("fields")
                                 @Expose
                                 var fields: GetProfileUrlResponseFields? = null,

                                 @SerializedName("url")
                                 @Expose
                                 var url: String = "")
