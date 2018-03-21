package com.rapidsos.shared.midas_confirmation_activity.presenter

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.location.Location
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter
import com.rapidsos.database.database.handlers.DbProfileHandler
import com.rapidsos.emergencydatasdk.data.profile.Profile
import com.rapidsos.emergencydatasdk.data.profile.values.EmergencyContactValue
import com.rapidsos.midas.data.Trigger
import com.rapidsos.midas.fail_safe.FailSafeTimer
import com.rapidsos.midas.flow.MidasFLow
import com.rapidsos.midas.flow.OnMidasFlowTriggerListener
import com.rapidsos.shared.midas_confirmation_activity.view.ConfirmationView
import com.rapidsos.shared.notification.PanicNotificationHandler
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.error
import pl.charmas.android.reactivelocation2.ReactiveLocationProvider

/**
 * @author Josias Sena
 */
class ConfirmationPresenterImpl(private val dbProfileHandler: DbProfileHandler,
                                private val locationProvider: ReactiveLocationProvider,
                                private val panicNotificationHandler: PanicNotificationHandler,
                                private val piOpenNativeDialer: PendingIntent) :
        MvpBasePresenter<ConfirmationView>(), ConfirmationPresenter, AnkoLogger {

    private val compositeDisposable = CompositeDisposable()
    private val gSon = GsonBuilder().setPrettyPrinting().create()

    private lateinit var midasFLow: MidasFLow

    companion object {
        private const val CALL_FLOW = "kronos_contacts"
        private const val COMPANY = "RapidSOS - Kronos"
    }

    @SuppressLint("MissingPermission")
    override fun triggerMidasCallFlow(midasFLow: MidasFLow) {
        this.midasFLow = midasFLow

        showMessage("Initiating call, please wait...")

        locationProvider.lastKnownLocation
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Location> {

                    override fun onSubscribe(disposable: Disposable) {
                        compositeDisposable.add(disposable)
                    }

                    override fun onError(throwable: Throwable) {
                        throwable.message?.let {
                            error(it)
                            showErrorMessage("Something went wrong. Using native dialer")
                        }
                    }

                    override fun onNext(location: Location) {
                        dbProfileHandler.getProfileWithUpdates().subscribe({ profile ->
                            if (profile.fullName == null ||
                                    profile.fullName?.value?.isEmpty() as Boolean) {
                                showErrorMessage("Please make sure your profile has " +
                                        "been filled out with your full name next time. " +
                                        "Using native dialer instead.")
                            } else if (profile.phoneNumber == null ||
                                    profile.phoneNumber?.value?.isEmpty() as Boolean) {
                                showErrorMessage("Please make sure your profile has " +
                                        "been filled out with your phone number. " +
                                        "Using native dialer instead.")
                            } else {
                                triggerCall(location, profile)
                            }
                        })
                    }

                    override fun onComplete() = Unit
                })
    }

    @SuppressLint("MissingPermission")
    private fun triggerCall(location: Location, profile: Profile) {
        val locationJsonObject = JsonObject().apply {
            addProperty("latitude", location.latitude)
            addProperty("longitude", location.longitude)
            addProperty("uncertainty", location.accuracy)
        }

        val user = JsonObject().apply {
            val fullName = profile.fullName?.value?.first()
            val phoneNumber = profile.phoneNumber?.value?.first()?.number

            addProperty("full_name", fullName)
            addProperty("phone_number", phoneNumber)
        }

        val contacts = arrayListOf<JsonObject>()

        profile.emergencyContact?.let {
            val contactList = it.value
            if (contactList.isNotEmpty()) {
                contactList.forEach { emgContact: EmergencyContactValue ->
                    val asEmgContactJson = JsonObject().apply {
                        addProperty("full_name", emgContact.fullName)
                        addProperty("phone_number", emgContact.phone)
                    }

                    contacts.add(asEmgContactJson)
                }
            }
        }

        val variablesJsonObject = JsonObject().apply {
            add("location", locationJsonObject)
            add("user", user)
            add("contacts", gSon.toJsonTree(contacts))
            addProperty("company", COMPANY)
        }

        val trigger = Trigger().apply {
            callFlow = CALL_FLOW
            variables = variablesJsonObject
        }

        midasFLow.trigger(trigger, object : OnMidasFlowTriggerListener {
            override fun onSuccess() {
                showMessage("Alert created successfully")
                compositeDisposable.clear()
            }

            override fun onError(error: String) {
                error(error)
                showErrorMessage("Something went wrong. Using native dialer instead.")

                FailSafeTimer.stop()

                compositeDisposable.clear()
            }
        })
    }

    override fun showMessage(message: String) = panicNotificationHandler.showNotification(message)

    override fun showErrorMessage(errorMessage: String) {
        piOpenNativeDialer.send()
        panicNotificationHandler.showNotification(errorMessage)
    }

    override fun unSubscribe() = compositeDisposable.clear()
}