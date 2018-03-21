package com.rapidsos.rain.data.network_response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * @author Josias Sena
 */
data class CallerId(@SerializedName("id") @Expose var id: Long = 0,
                    @SerializedName("caller_id") @Expose var callerId: String = "")