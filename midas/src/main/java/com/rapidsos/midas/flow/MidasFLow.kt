package com.rapidsos.midas.flow

import android.content.Context
import com.rapidsos.midas.api.ApiBuilder
import com.rapidsos.midas.api.MidasApi
import com.rapidsos.midas.api.MidasRetrofitConfigurations
import com.rapidsos.midas.data.Trigger
import com.rapidsos.midas.fail_safe.FailSafeTimer
import com.rapidsos.midas.helpers.MIDAS_HOST
import io.reactivex.SingleSource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.AnkoLogger
import retrofit2.Response

/**
 * Handles the triggering of the midas flow specific to ERA.
 *
 * @author Josias Sena
 */
class MidasFLow(private val context: Context) : AnkoLogger {

    private val retrofitConfigs = MidasRetrofitConfigurations(context)
    private var api: MidasApi = ApiBuilder.buildApi(MIDAS_HOST, retrofitConfigs)

    private lateinit var triggerListener: OnMidasFlowTriggerListener

    /**
     * Trigger the midas flow by providing a [Trigger].
     *
     * Example usage for a random custom flow which requires your current location,
     * emergency contacts, and your name and phone number.
     *
     * ```
     *private fun triggerCall(location: Location) {
     *      val locationJsonObject = JsonObject().apply {
     *      addProperty("latitude", location.latitude)
     *      addProperty("longitude", location.longitude)
     *      addProperty("uncertainty", location.accuracy)
     *  }
     *
     *  val user = JsonObject().apply {
     *      addProperty("full_name", "John Doe")
     *      addProperty("phone_number", "15555555555")
     *  }
     *
     *  val contacts = arrayListOf<JsonObject>()
     *      contacts.add(JsonObject().apply {
     *      addProperty("full_name", "Mom")
     *      addProperty("phone_number", "12345678912")
     *  })
     *
     *  val variablesJsonObject = JsonObject().apply {
     *      add("location", locationJsonObject)
     *      add("user", user)
     *      add("contacts", gson.toJsonTree(contacts))
     *      addProperty("company", "RapidSOS")
     *  }
     *
     *  val trigger = Trigger().apply {
     *      callFlow = "flow_call_name"
     *      variables = variablesJsonObject
     *  }
     *
     *  midasFLow.trigger(trigger, object : OnMidasFlowTriggerListener {
     *      override fun onSuccess() {
     *          showMessage("Alert created successfully")
     *          compositeDisposable.clear()
     *      }
     *
     *      override fun onError(error: String) {
     *          error(error)
     *          showErrorMessage("Something went wrong. Using native dialer instead.")
     *          compositeDisposable.clear()
     *      }
     *  })
     *}
     *
     * ```
     * @param trigger the object use to specify the flow to trigger
     * @param triggerListener the triggerListener that gets notified if the flow trigger was successful or not
     * @see Trigger
     * @see OnMidasFlowTriggerListener
     */
    fun trigger(trigger: Trigger, triggerListener: OnMidasFlowTriggerListener) {
        this.triggerListener = triggerListener

        FailSafeTimer.start(context)

        // Get an access token
        api.getAccessToken()
                .subscribeOn(Schedulers.io())
                .onErrorResumeNext { throwable ->
                    SingleSource {
                        triggerListener.onError("Failure getting access token: ${throwable.message}")
                    }
                }
                // Only move on if we got an access token successfully.
                .filter { response ->
                    if (response.isSuccessful) {
                        true
                    } else {
                        logError(response)
                        false
                    }
                }
                .map { response -> response.body() }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { oauthResponse ->
                    oauthResponse?.let {
                        // Perform the flow trigger
                        triggerFlow("Bearer ${oauthResponse.accessToken}", trigger)
                    }
                }
    }

    private fun triggerFlow(token: String, trigger: Trigger) =
            api.triggerFlow(token, trigger)
                    .subscribeOn(Schedulers.io())
                    .onErrorResumeNext { throwable ->
                        SingleSource {
                            triggerListener.onError("Failure triggering flow: ${throwable.message}")
                        }
                    }
                    .filter { response ->
                        if (response.isSuccessful) {
                            true
                        } else {
                            logError(response)
                            false
                        }
                    }
                    .map { response -> response.body() }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { triggerListener.onSuccess() }

    private fun <T> logError(response: Response<T>) {
        val errorBody = response.errorBody()?.string()
        val message = response.message()
        val code = response.code()

        triggerListener.onError("$code $message $errorBody")
    }

}