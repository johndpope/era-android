package com.rapidsos.midas.helpers

import android.util.Base64

/**
 * @author Josias Sena
 */
object AuthCredentials {

    /**
     * Get 'Basic' credentials for midas network calls
     */
    fun getBasicCredentials(): String {
        val credentials = MIDAS_CLIENT_ID + ":" + MIDAS_CLIENT_SECRET
        return "Basic " + Base64.encodeToString(credentials.toByteArray(), Base64.NO_WRAP)
    }

}
