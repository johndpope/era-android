package com.rapidsos.rain.auth

import com.rapidsos.rain.data.network_response.SessionToken
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Verifies if a Oauth token is valid or not
 *
 * @author Josias Sena
 */
class OauthTokenVerifier {

    companion object {

        /**
         * Checks if the [token] is expired or not based on its expiration date
         *
         * @param token the token to verify
         * @return true if the token is a valid token, and has not expired yet. False otherwise.
         */
        fun isTokenExpired(token: SessionToken): Boolean {
            val currentDate = Date(System.currentTimeMillis())
            val expiresInMillis = TimeUnit.SECONDS.toMillis(token.expiresIn.toLong())
            val expiryDate = Date(token.issuedAt.toLong().plus(expiresInMillis))
            return currentDate.after(expiryDate)
        }
    }

}