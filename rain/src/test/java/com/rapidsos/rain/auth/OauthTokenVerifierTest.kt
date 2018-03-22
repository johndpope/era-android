package com.rapidsos.rain.auth

import com.rapidsos.rain.data.network_response.SessionToken
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * @author Josias Sena
 */
class OauthTokenVerifierTest {

    companion object {
        private const val ONE_DAY_MILLI_SECONDS = 86400000
        private const val ONE_HOUR_IN_SECONDS = 3600
    }

    @Test
    fun testIsTokenExpiredWithAnExpiredToken() {
        val expiredToken = SessionToken().apply {
            expiresIn = "$ONE_HOUR_IN_SECONDS"
            issuedAt = "${System.currentTimeMillis().minus(ONE_DAY_MILLI_SECONDS)}"
        }

        assertTrue(OauthTokenVerifier.isTokenExpired(expiredToken))
    }

    @Test
    fun testIsTokenExpiredWithValidToken() {
        val expiredToken = SessionToken().apply {
            expiresIn = "$ONE_HOUR_IN_SECONDS"
            issuedAt = "${System.currentTimeMillis().plus(ONE_DAY_MILLI_SECONDS)}"
        }

        assertFalse(OauthTokenVerifier.isTokenExpired(expiredToken))
    }

}