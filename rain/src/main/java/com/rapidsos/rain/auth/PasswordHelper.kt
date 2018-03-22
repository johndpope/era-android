package com.rapidsos.rain.auth

import com.google.gson.JsonObject
import com.rapidsos.rain.api.RainApi
import com.rapidsos.rain.api.RainCredentials
import com.rapidsos.rain.connection.ConnectionVerifier
import io.reactivex.MaybeObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.error
import retrofit2.Response

/**
 * Used to reset a users password.
 *
 * @author Josias Sena
 */
class PasswordHelper(private val api: RainApi) : AnkoLogger {

    private lateinit var listener: MaybeObserver<Response<ResponseBody>>

    /**
     * Reset a users password based on the email.
     *
     * @param email the email to send the password reset link to
     * @param basicCredentials Basic credentials required to get an access token.
     * @param listener the lister that gets notified on successful reset email sent, or on error.
     */
    fun resetPassword(email: String, basicCredentials: String = RainCredentials.getBasicCredentials(),
                      listener: MaybeObserver<Response<ResponseBody>>) {
        this.listener = listener

        ConnectionVerifier.isConnectedToInternet(Consumer { isInternetAvailable ->
            if (isInternetAvailable) {
                api.getAccessToken(basicCredentials)
                        .subscribeOn(Schedulers.io())
                        .filter { filter(it) }
                        .map { it.body() }
                        .flatMapSingle {
                            val emailJson = JsonObject().apply {
                                addProperty("email", email)
                            }

                            api.resetPassword("Bearer ${it.accessToken}", emailJson)
                        }
                        .filter { filter(it) }
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(listener)
            } else {
                val throwable = Throwable("No network connection. Try again later.")
                listener.onError(throwable)
            }
        })
    }

    private fun <T> filter(response: Response<T>): Boolean {
        return if (response.isSuccessful) {
            true
        } else {
            val errorBody = response.errorBody()?.string()
            val message = response.message()
            val code = response.code()

            try {
                error("Error: $code $message $errorBody")
            } catch (e: RuntimeException) {
            }

            listener.onError(Throwable(message))

            false
        }
    }

}