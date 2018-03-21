package com.rapidsos.rain.auth

import com.google.gson.JsonObject
import com.rapidsos.rain.api.RainApi
import com.rapidsos.rain.api.RainCredentials
import com.rapidsos.rain.connection.ConnectionVerifier
import com.rapidsos.rain.data.user.User
import com.rapidsos.rain.helpers.BAD_REQUEST_CODE
import com.rapidsos.rain.helpers.TOO_MANY_REQUEST_CODE
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.error
import retrofit2.Response

/**
 * Used for registering new Additional Data user accounts that will provide personal and device
 * data they want available during emergencies
 *
 * @author Josias Sena
 */
class RegistrationHandler(private val api: RainApi) : AnkoLogger {

    companion object {
        private const val USERNAME = "username"
        private const val EMAIL = "email"
        private const val PWD = "password"
    }

    /**
     * Register the user with the RAIN API
     *
     * @param user the user to register
     * @param observer the [Observer] that gets notified when the user has been successfully
     * registered  or if there is an error when registering the user
     */
    fun register(user: User, observer: Observer<Response<User>>) {
        ConnectionVerifier.isConnectedToInternet(Consumer { isInternetAvailable ->
            with(user) {
                if (isInternetAvailable) {
                    api.getAccessToken(RainCredentials.getBasicCredentials())
                            .subscribeOn(Schedulers.io())
                            .filter { response -> response.isSuccessful }
                            .flatMapObservable({ response ->
                                val accessToken = response.body()?.accessToken
                                val userAsJson = JsonObject().apply {
                                    addProperty(USERNAME, username)
                                    addProperty(EMAIL, email)
                                    addProperty(PWD, password)
                                }

                                api.register("Bearer $accessToken", userAsJson)
                            })
                            .filter { filter(it, observer) }
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(observer)
                } else {
                    val throwable = Throwable("No network connection. Try again later.")
                    observer.onError(throwable)
                }
            }
        })
    }

    private fun filter(response: Response<User>, observer: Observer<Response<User>>): Boolean {
        return if (response.isSuccessful) {
            true
        } else {
            val errorBody = response.errorBody()?.string()
            val message = response.message()
            val code = response.code()

            try {
                error("Registration error: $code $message $errorBody")
            } catch (ignore: RuntimeException) {
            }

            when (code) {
                BAD_REQUEST_CODE -> errorBody?.let {
                    if (it.contains("already exists", ignoreCase = true)) {
                        val throwable = Throwable("User with the same username or email already exists")
                        observer.onError(throwable)
                    } else {
                        val throwable = Throwable("Please try again later.")
                        observer.onError(throwable)
                    }
                }
                TOO_MANY_REQUEST_CODE ->
                    observer.onError(Throwable("Too many requests. Please try again later."))
                else -> observer.onError(Throwable("Something went wrong, please try again."))
            }

            false
        }
    }
}