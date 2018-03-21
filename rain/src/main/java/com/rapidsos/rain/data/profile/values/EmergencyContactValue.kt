package com.rapidsos.rain.data.profile.values

import com.google.gson.JsonObject
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class EmergencyContactValue(@SerializedName("label") @Expose var label: String = "",
                                 @SerializedName("full_name") @Expose var fullName: String = "",
                                 @SerializedName("phone") @Expose var phone: String = "",
                                 @SerializedName("email") @Expose var email: String = "",
                                 @SerializedName("note") @Expose var note: String = "") {

    fun asEmgContactJson(): JsonObject {
        return JsonObject().apply {
            addProperty("full_name", fullName)
            addProperty("phone_number", phone)
        }
    }
}
