package com.rapidsos.rain.auth

import com.rapidsos.rain.api.RainApi
import com.rapidsos.rain.data.network_response.SessionToken
import com.rapidsos.rain.data.user.User
import io.reactivex.Observer
import org.jetbrains.anko.AnkoLogger

/**
 * Wrapper around the [LoginController] for clarification on getting a new oauth token. To get a new
 * oauth token a user is pretty much signing on again. But instead of using the [LoginController] it
 * is easier to use this class instead to understand what is happening.
 *
 * @author Josias Sena
 */
class OauthTokenProvider(api: RainApi) : AnkoLogger {

    private val loginController = LoginController(api)

    /**
     * Provides a brand new oauth token
     *
     * @param user the user the token is being fetched for
     * @param observer the listener used to notify of success and errors when fetching the
     * oauth token
     */
    fun getNewOauthToken(user: User, observer: Observer<SessionToken?>) {
        loginController.login(user, observer)
    }

}