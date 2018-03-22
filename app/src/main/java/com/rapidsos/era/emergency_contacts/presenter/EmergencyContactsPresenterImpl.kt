package com.rapidsos.era.emergency_contacts.presenter

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter
import com.rapidsos.database.database.handlers.DbProfileHandler
import com.rapidsos.database.database.handlers.DbSessionTokenHandler
import com.rapidsos.database.database.handlers.DbUserHandler
import com.rapidsos.emergencydatasdk.auth.login.SessionManager
import com.rapidsos.emergencydatasdk.data.network_response.SessionToken
import com.rapidsos.emergencydatasdk.data.profile.EmergencyContact
import com.rapidsos.emergencydatasdk.data.profile.Profile
import com.rapidsos.emergencydatasdk.data.profile.values.EmergencyContactValue
import com.rapidsos.emergencydatasdk.internal.helpers.network.SessionTokenVerifier
import com.rapidsos.emergencydatasdk.profile.ProfileUpdater
import com.rapidsos.era.application.App
import com.rapidsos.era.emergency_contacts.view.EmergencyContactsView
import com.rapidsos.utils.preferences.EraPreferences
import io.reactivex.MaybeObserver
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.error
import javax.inject.Inject

/**
 * @author Josias Sena
 */
class EmergencyContactsPresenterImpl : MvpBasePresenter<EmergencyContactsView>(),
        EmergencyContactsPresenter, AnkoLogger {

    private val sessionManager = SessionManager()
    private val profileUpdater = ProfileUpdater()
    private val compositeDisposable = CompositeDisposable()

    @Inject
    lateinit var dbUserHandler: DbUserHandler

    @Inject
    lateinit var dbSessionTokenHandler: DbSessionTokenHandler

    @Inject
    lateinit var dbProfileHandler: DbProfileHandler

    @Inject
    lateinit var preferences: EraPreferences

    init {
        App.component.inject(this)
    }

    override fun getEmergencyContacts() {
        showLoading()

        dbProfileHandler.getProfileWithoutUpdates().subscribe { profile ->
            hideLoading()

            if (isViewAttached) {
                profile.emergencyContact?.let {
                    view?.onGotEmergencyContacts(it.value)
                }
            }
        }
    }

    override fun addEmgContact(contact: EmergencyContactValue) {
        showLoading()

        dbProfileHandler.getProfileWithoutUpdates().subscribe { profile ->
            profile.apply {
                if (emergencyContact == null) {
                    emergencyContact = EmergencyContact()
                }

                emergencyContact?.value?.add(contact)
            }

            dbSessionTokenHandler.getSessionToken().subscribe({ token ->
                if (!SessionTokenVerifier.isTokenExpired(token)) {
                    updateProfile(token, profile)
                } else {
                    getTokenThenUpdateProfile(profile)
                }
            })
        }
    }

    override fun updateEmgContact(index: Int, contact: EmergencyContactValue) {
        showLoading()

        dbProfileHandler.getProfileWithoutUpdates().subscribe { profile ->
            profile.apply {
                emergencyContact?.value?.set(index, contact)
            }

            dbSessionTokenHandler.getSessionToken().subscribe({ token ->
                if (!SessionTokenVerifier.isTokenExpired(token)) {
                    updateProfile(token, profile)
                } else {
                    getTokenThenUpdateProfile(profile)
                }
            })
        }
    }

    private fun getTokenThenUpdateProfile(profile: Profile) {
        dbUserHandler.getCurrentUser().subscribe({ user ->
            sessionManager.getSessionToken(user.username as String, user.password as String)
                    .subscribe(object : MaybeObserver<SessionToken?> {

                        override fun onSubscribe(disposable: Disposable) {
                            compositeDisposable.add(disposable)
                        }

                        override fun onError(error: Throwable) {
                            hideLoading()

                            error(error.message)

                            if (isViewAttached) {
                                error.message?.let { view?.showError(it) }
                            }
                        }

                        override fun onSuccess(response: SessionToken) {
                            dbSessionTokenHandler.insertSessionToken(response)
                            updateProfile(response, profile)
                        }

                        override fun onComplete() = Unit
                    })
        })
    }

    private fun updateProfile(token: SessionToken, profile: Profile) =
            compositeDisposable.add(profileUpdater.updatePersonalInfo(token, profile)
                    .subscribe({ updatedProfile: Profile? ->
                        updatedProfile?.let {
                            dbProfileHandler.updateProfile(it)

                            if (isViewAttached) {
                                it.emergencyContact?.let {
                                    view?.onGotEmergencyContacts(it.value)
                                }

                                view?.hideLoading()
                            }
                        }
                    }, { throwable: Throwable? ->
                        error(throwable?.message, throwable)
                        hideLoading()
                    }))

    override fun deleteContact(contact: EmergencyContactValue) {
        showLoading()

        dbProfileHandler.getProfileWithoutUpdates().subscribe { profile ->
            profile.apply {
                emergencyContact?.value?.remove(contact)

                dbSessionTokenHandler.getSessionToken().subscribe({ token ->
                    if (!SessionTokenVerifier.isTokenExpired(token)) {
                        updateProfile(token, profile)
                    } else {
                        getTokenThenUpdateProfile(profile)
                    }
                })
            }
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