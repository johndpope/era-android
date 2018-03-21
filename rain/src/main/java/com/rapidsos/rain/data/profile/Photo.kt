package com.rapidsos.rain.data.profile

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.rapidsos.rain.data.profile.values.PhotoValue

data class Photo(@SerializedName("value") @Expose var value: List<PhotoValue>? = null,
                 @SerializedName("type") @Expose var type: String? = null,
                 @SerializedName("display_name") @Expose var displayName: String? = null)
