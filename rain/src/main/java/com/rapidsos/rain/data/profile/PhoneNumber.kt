package com.rapidsos.rain.data.profile

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.rapidsos.rain.data.profile.values.PhoneNumberValue

data class PhoneNumber(@SerializedName("value") @Expose var value: List<PhoneNumberValue> = arrayListOf(),
                       @SerializedName("type") @Expose var type: String = "",
                       @SerializedName("display_name") @Expose var displayName: String = "")