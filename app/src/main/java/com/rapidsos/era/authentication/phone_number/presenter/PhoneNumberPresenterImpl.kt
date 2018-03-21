package com.rapidsos.era.authentication.phone_number.presenter

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter
import com.rapidsos.database.database.handlers.DbProfileHandler
import com.rapidsos.database.database.handlers.DbSessionTokenHandler
import com.rapidsos.database.database.handlers.DbUserHandler
import com.rapidsos.emergencydatasdk.auth.login.SessionManager
import com.rapidsos.emergencydatasdk.data.network_response.CallerId
import com.rapidsos.emergencydatasdk.data.network_response.SessionToken
import com.rapidsos.emergencydatasdk.data.profile.Profile
import com.rapidsos.emergencydatasdk.internal.helpers.network.SessionTokenVerifier
import com.rapidsos.emergencydatasdk.pin.provider.PinProvider
import com.rapidsos.emergencydatasdk.pin.validator.PinValidator
import com.rapidsos.emergencydatasdk.profile.ProfileProvider
import com.rapidsos.era.application.App
import com.rapidsos.era.authentication.phone_number.view.PhoneNumberView
import com.rapidsos.utils.preferences.EraPreferences
import io.reactivex.MaybeObserver
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.error
import retrofit2.Response
import javax.inject.Inject

/**
 * @author Josias Sena
 */
class PhoneNumberPresenterImpl : MvpBasePresenter<PhoneNumberView>(), PhoneNumberPresenter,
        AnkoLogger {

    private val compositeDisposable = CompositeDisposable()
    private val profileProvider = ProfileProvider()
    private val sessionManager = SessionManager()
    private val pinValidator = PinValidator()
    private val pinProvider = PinProvider()

    @Inject
    lateinit var preferences: EraPreferences

    @Inject
    lateinit var dbUserHandler: DbUserHandler

    @Inject
    lateinit var dbSessionTokenHandler: DbSessionTokenHandler

    @Inject
    lateinit var dbProfileHandler: DbProfileHandler

    init {
        App.component.inject(this)
    }

    /**
     * Save the phone number to the shared preferences.
     */
    override fun saveDevicePhoneNumber(phoneNumber: String) {
        if (phoneNumber.startsWith("1")) {
            preferences.setCurrentDevicePhoneNumber(phoneNumber)
        } else {
            preferences.setCurrentDevicePhoneNumber("1$phoneNumber")
        }
    }

    override fun requestPin(phoneNumber: String) {
        showLoading()

        saveDevicePhoneNumber(phoneNumber)
        val currentDevicePhoneNumber = preferences.getCurrentDevicePhoneNumber()

        dbSessionTokenHandler.getSessionToken().subscribe({ token ->
            if (SessionTokenVerifier.isTokenExpired(token)) {
                dbUserHandler.getCurrentUser()
                        .flatMap {
                            sessionManager.getSessionToken(it.username as String, it.username as String)
                        }
                        .subscribe(object : MaybeObserver<SessionToken?> {

                            override fun onSubscribe(d: Disposable) {
                                compositeDisposable.add(d)
                            }

                            override fun onError(e: Throwable) {
                                hideLoading()

                                if (isViewAttached) {
                                    e.message?.let { view?.showError(it) }
                                }
                            }

                            override fun onSuccess(sessionToken: SessionToken) {
                                createCallerId(sessionToken, currentDevicePhoneNumber)
                            }

                            override fun onComplete() {
                            }
                        })
            } else {
                createCallerId(token, currentDevicePhoneNumber)
            }
        })
    }

    private fun createCallerId(token: SessionToken, phoneNumber: String) {
        pinProvider.requestPin(token, phoneNumber)
                .subscribe(object : MaybeObserver<Response<CallerId>> {

                    override fun onSubscribe(disposable: Disposable) {
                        compositeDisposable.add(disposable)
                    }

                    override fun onError(throwable: Throwable) {
                        hideLoading()

                        error(throwable.message, throwable)

                        if (isViewAttached) {
                            throwable.message?.let { view?.showError(it) }
                        }
                    }

                    override fun onSuccess(callerId: Response<CallerId>) {
                        showPinViews()
                        hideLoading()
                    }

                    override fun onComplete() {
                    }
                })
    }

    private fun showPinViews() {
        if (isViewAttached) {
            view?.showPinViews()
        }
    }

    override fun validatePin(pin: Int) {
        showLoading()

        val currentDevicePhoneNumber = preferences.getCurrentDevicePhoneNumber()

        dbSessionTokenHandler.getSessionToken().subscribe({ token ->
            if (SessionTokenVerifier.isTokenExpired(token)) {
                dbUserHandler.getCurrentUser()
                        .flatMap {
                            sessionManager.getSessionToken(it.username as String, it.username as String)
                        }
                        .subscribe(object : MaybeObserver<SessionToken?> {

                            override fun onSubscribe(d: Disposable) {
                                compositeDisposable.add(d)
                            }

                            override fun onError(e: Throwable) {
                                hideLoading()

                                if (isViewAttached) {
                                    e.message?.let { view?.showError(it) }
                                }
                            }

                            override fun onSuccess(sessionToken: SessionToken) {
                                validatePin(sessionToken, currentDevicePhoneNumber, pin)
                            }

                            override fun onComplete() = Unit
                        })
            } else {
                validatePin(token, currentDevicePhoneNumber, pin)
            }
        })
    }

    private fun validatePin(token: SessionToken, currentDevicePhoneNumber: String, pin: Int) {
        pinValidator.validatePin(token, currentDevicePhoneNumber, pin)
                .flatMap { profileProvider.getPersonalInfo(token) }
                .subscribe(object : MaybeObserver<Profile?> {

                    override fun onSubscribe(d: Disposable) {
                        compositeDisposable.add(d)
                    }

                    override fun onError(throwable: Throwable) {
                        hideLoading()

                        if (isViewAttached) {
                            throwable.message?.let { view?.showError(it) }
                        }
                    }

                    override fun onSuccess(profile: Profile) {
                        preferences.setIsPinValidated(true)
                        dbProfileHandler.insertProfile(profile)

                        goToMainScreen()
                    }

                    override fun onComplete() = hideLoading()
                })
    }

    private fun goToMainScreen() {
        if (isViewAttached) {
            view?.hideLoading()
            view?.goToMainScreen()
        }
    }

    private fun showLoading() {
        if (isViewAttached) {
            view?.showLoading()
        }
    }

    private fun hideLoading() {
        if (isViewAttached) {
            view?.hideLoading()
        }
    }

}