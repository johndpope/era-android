package com.rapidsos.midas.data

import com.google.gson.JsonObject
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * @author Josias Sena
 */
data class Trigger(@SerializedName("callflow") @Expose var callFlow: String = "",
                   @SerializedName("variables") @Expose var variables: JsonObject = JsonObject())