package com.rapidsos.rain.data.profile

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.rapidsos.rain.data.profile.values.EmailValue

data class Email(@SerializedName("value") @Expose var value: List<EmailValue> = arrayListOf(),
                 @SerializedName("type") @Expose var type: String = "",
                 @SerializedName("display_name") @Expose var displayName: String = "")
