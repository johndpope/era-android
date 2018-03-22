package com.rapidsos.rain.pin

import com.google.gson.JsonObject
import com.rapidsos.rain.api.RainApi
import com.rapidsos.rain.data.network_response.CallerId
import com.rapidsos.rain.data.network_response.SessionToken
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.error
import retrofit2.Response

/**
 * @author Josias Sena
 */
class PinProvider(private val api: RainApi) : AnkoLogger {

    /**
     * Request for a new pin
     *
     * @param token the session token to make the network request
     * @param callerId the phone number to send the pin to
     * @param observer the observer to be notified of error, or success
     */
    fun requestPin(token: SessionToken, callerId: String, observer: Observer<Response<CallerId>>) {
        val body = JsonObject().apply {
            addProperty("caller_id", callerId)
        }

        val accessToken = "Bearer ${token.accessToken}"
        api.createCallerId(accessToken, body)
                .subscribeOn(Schedulers.io())
                .doOnError({ throwable -> observer.onError(throwable) })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response ->
                    if (response.isSuccessful) {
                        observer.onNext(response)
                        observer.onComplete()
                    } else {
                        val errorBody = response.errorBody()?.string()
                        val message = response.message()
                        val code = response.code()

                        error("RequestPin error: $code $message $errorBody")

                        observer.onError(Throwable(message))
                    }
                }, { throwable: Throwable? ->
                    throwable?.let { observer.onError(it) }
                })

    }

}