package com.rapidsos.rain.data.profile

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Allergy(@SerializedName("value") @Expose var value: List<String> = arrayListOf(),
                   @SerializedName("type") @Expose var type: String = "",
                   @SerializedName("display_name") @Expose var displayName: String = "")
