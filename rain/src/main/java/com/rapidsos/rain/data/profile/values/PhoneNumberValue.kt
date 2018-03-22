package com.rapidsos.rain.data.profile.values

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class PhoneNumberValue(@SerializedName("label") @Expose var label: String = "",
                            @SerializedName("number") @Expose var number: String = "",
                            @SerializedName("note") @Expose var note: String = "")
