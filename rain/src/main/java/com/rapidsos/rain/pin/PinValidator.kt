package com.rapidsos.rain.pin

import com.google.gson.JsonObject
import com.rapidsos.rain.api.RainApi
import com.rapidsos.rain.data.network_response.CallerId
import com.rapidsos.rain.data.network_response.SessionToken
import com.rapidsos.rain.data.profile.Profile
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.error
import retrofit2.Response
import java.util.concurrent.TimeUnit

/**
 * @author Josias Sena
 */
class PinValidator(private val api: RainApi) : AnkoLogger {

    private lateinit var observer: Observer<Profile>

    /**
     * Validate a pin number
     *
     * @param token the session token to make the network request
     * @param callerId the phone number the pin was sent to
     * @param pin the pin number to valdiate
     * @param observer the observer to be notified of error, or success
     */
    fun validatePin(token: SessionToken, callerId: String, pin: Int, observer: Observer<Profile>) {
        this.observer = observer

        val body = getBody(callerId, pin)
        val accessToken = "Bearer ${token.accessToken}"

        api.validateCallerId(accessToken, body)
                .subscribeOn(Schedulers.io())
                .filter { filterValidation(it) }
                .flatMap { api.getPersonalInfo(accessToken).toMaybe() }
                .filter { filterGettingPersonalInfo(it) }
                .timeout(15, TimeUnit.SECONDS)
                .map { response: Response<Profile> -> response.body() }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ profile ->
                    profile?.let {
                        observer.onNext(profile)
                    }

                    observer.onComplete()
                })
    }

    private fun filterGettingPersonalInfo(response: Response<Profile>): Boolean {
        return if (response.isSuccessful) {
            true
        } else {
            val errorBody = response.errorBody()?.string()
            val message = response.message()
            val code = response.code()

            error("ValidatePin error: $code $message $errorBody")

            observer.onError(Throwable(message))
            false
        }
    }

    private fun filterValidation(response: Response<CallerId>): Boolean {
        return if (response.isSuccessful) {
            true
        } else {
            val errorBody = response.errorBody()?.string()
            val message = response.message()
            val code = response.code()

            error("ValidatePin error: $code $message $errorBody")

            errorBody?.let {
                if (it.contains("Validation code is invalid.")) {
                    observer.onError(Throwable("Validation code is invalid."))
                } else {
                    observer.onError(Throwable("Something bad happen, please try again"))
                }
            }

            false
        }
    }

    private fun getBody(callerId: String, pin: Int): JsonObject {
        return JsonObject().apply {
            addProperty("caller_id", callerId)
            addProperty("validation_code", pin.toString())
        }
    }

}