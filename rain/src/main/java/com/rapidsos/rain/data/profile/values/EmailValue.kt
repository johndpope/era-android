package com.rapidsos.rain.data.profile.values

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class EmailValue(@SerializedName("label") @Expose var label: String = "",
                      @SerializedName("email_address") @Expose var emailAddress: String = "",
                      @SerializedName("note") @Expose var note: String = "")
