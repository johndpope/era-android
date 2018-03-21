package com.rapidsos.rain.data.profile

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.rapidsos.rain.data.profile.values.AddressValue

data class Address(@SerializedName("value") @Expose var value: List<AddressValue>? = listOf(),
                   @SerializedName("type") @Expose var type: String? = "",
                   @SerializedName("display_name") @Expose var displayName: String? = "")