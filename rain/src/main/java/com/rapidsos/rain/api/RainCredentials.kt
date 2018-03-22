package com.rapidsos.rain.api

import android.util.Base64
import com.rapidsos.rain.helpers.CLIENT_ID
import com.rapidsos.rain.helpers.CLIENT_SECRET

/**
 * @author Josias Sena
 */
class RainCredentials {

    /**
     * Provide 'Basic' credentials for RAIN apis
     */
    companion object {
        fun getBasicCredentials(): String {
            val credentials = CLIENT_ID + ":" + CLIENT_SECRET
            return "Basic " + Base64.encodeToString(credentials.toByteArray(), Base64.NO_WRAP)
        }
    }
}
