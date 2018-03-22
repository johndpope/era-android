package com.rapidsos.rain.data.profile.values

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class PhotoValue(@SerializedName("label") @Expose var label: String = "",
                      @SerializedName("url") @Expose var url: String = "",
                      @SerializedName("note") @Expose var note: String = "")
