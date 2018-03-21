package com.rapidsos.rain.data.profile

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Birthday(@SerializedName("value") @Expose var value: List<Long> = arrayListOf(),
                    @SerializedName("type") @Expose var type: String = "",
                    @SerializedName("display_name") @Expose var displayName: String = "",
                    @SerializedName("units") @Expose var units: String = "")
