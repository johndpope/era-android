package com.rapidsos.rain.auth

import com.rapidsos.rain.api.RainApi
import com.rapidsos.rain.data.network_response.SessionToken
import com.rapidsos.rain.data.user.User
import com.rapidsos.rain.helpers.TOO_MANY_REQUEST_CODE
import com.rapidsos.rain.helpers.UNAUTHORIZED_REQUEST_CODE
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.error
import retrofit2.Response

/**
 * @author Josias Sena
 */
class LoginController(private val api: RainApi) : AnkoLogger {

    /**
     * Login the user with the RAIN API
     *
     * @param user the user to login
     * @param observer the [Observer] that gets notified when the user has been successfully
     * login or if there is an error when trying to log the user in
     */
    fun login(user: User, observer: Observer<SessionToken?>) {
        with(user) {
            api.login(username = username, password = password)
                    .subscribeOn(Schedulers.io())
                    .filter { filter(it, observer) }
                    .map { it.body() }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(observer)
        }
    }

    private fun filter(response: Response<SessionToken>, observer: Observer<SessionToken?>): Boolean {
        return if (response.isSuccessful) {
            true
        } else {
            val errorBody = response.errorBody()?.string()
            val message = response.message()
            val code = response.code()

            try {
                error("Login error: $code $message $errorBody")
            } catch (ignore: RuntimeException) {
            }

            when (code) {
                UNAUTHORIZED_REQUEST_CODE -> observer.onError(Throwable("Invalid user credentials."))
                TOO_MANY_REQUEST_CODE -> observer.onError(Throwable("Too many requests. Please try again later."))
                else -> observer.onError(Throwable("Something went wrong, please try again."))
            }

            false
        }
    }
}