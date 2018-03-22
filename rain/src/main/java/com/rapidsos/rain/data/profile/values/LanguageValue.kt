package com.rapidsos.rain.data.profile.values

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class LanguageValue(@SerializedName("language_code") @Expose var languageCode: String = "",
                         @SerializedName("preference") @Expose var preference: Int = 0)
