package com.rapidsos.era.profile.edit_profile.presenter

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter
import com.rapidsos.database.database.handlers.DbProfileHandler
import com.rapidsos.database.database.handlers.DbSessionTokenHandler
import com.rapidsos.database.database.handlers.DbUserHandler
import com.rapidsos.emergencydatasdk.auth.login.SessionManager
import com.rapidsos.emergencydatasdk.data.network_response.SessionToken
import com.rapidsos.emergencydatasdk.data.profile.Profile
import com.rapidsos.emergencydatasdk.internal.helpers.network.SessionTokenVerifier
import com.rapidsos.emergencydatasdk.profile.ProfileUpdater
import com.rapidsos.era.application.App
import com.rapidsos.era.profile.edit_profile.view.EditProfileView
import com.rapidsos.utils.preferences.EraPreferences
import io.reactivex.MaybeObserver
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.error
import org.jetbrains.anko.uiThread
import javax.inject.Inject

/**
 * @author Josias Sena
 */
class EditProfilePresenterImpl @Inject constructor() :
        MvpBasePresenter<EditProfileView>(), EditProfilePresenter, AnkoLogger {

    private val compositeDisposable = CompositeDisposable()
    private val sessionManager = SessionManager()
    private val personalInfoUpdater = ProfileUpdater()

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

    override fun getProfileInformation() {
        showLoading()

        dbProfileHandler.getProfileWithUpdates().subscribe({ profile ->
            if (isViewAttached) {
                view?.displayProfile(profile)
            }

            hideLoading()
        })
    }

    override fun saveProfileInfo(profile: Profile) {
        showLoading()

        dbSessionTokenHandler.getSessionToken().subscribe({ token ->
            if (!SessionTokenVerifier.isTokenExpired(token)) {
                updateProfile(token, profile)
            } else {
                dbUserHandler.getCurrentUser().subscribe({ user ->

                    val username = user.username.toString()
                    val password = user.password.toString()

                    if (!username.isEmpty() && !password.isEmpty()) {
                        sessionManager.getSessionToken(username, password)
                                .subscribe(object : MaybeObserver<SessionToken?> {

                                    override fun onSubscribe(disposable: Disposable) {
                                        compositeDisposable.add(disposable)
                                    }

                                    override fun onError(error: Throwable) {
                                        error("Error saving profile", error)
                                        hideLoading()

                                        if (isViewAttached) {
                                            error.message?.let { view?.showError(it) }
                                        }
                                    }

                                    override fun onSuccess(sessionToken: SessionToken) {
                                        sessionToken.let {
                                            dbSessionTokenHandler.insertSessionToken(it)
                                            updateProfile(it, profile)
                                        }
                                    }

                                    override fun onComplete() = hideLoading()
                                })
                    } else {
                        error("The username: $username or password: $password seem to be empty")
                    }
                })
            }
        })
    }

    private fun updateProfile(token: SessionToken, profile: Profile) {
        personalInfoUpdater.updatePersonalInfo(token, profile)
                .subscribe(object : MaybeObserver<Profile?> {

                    override fun onSubscribe(disposable: Disposable) {
                        compositeDisposable.add(disposable)
                    }

                    override fun onError(e: Throwable) {
                        error("Error updating profile", e)
                        hideLoading()
                    }

                    override fun onSuccess(profile: Profile) {
                        dbProfileHandler.updateProfile(profile)

                        if (isViewAttached) {
                            view?.onSuccessUpdatingProfile()
                        }
                    }

                    override fun onComplete() = hideLoading()
                })
    }

    override fun getHeightInInches(heightFeet: String, heightInches: String): Int {

        var totalInches = 0

        if (heightFeet.isNotEmpty()) {
            totalInches = totalInches.plus(heightFeet.toInt().times(12))
        }

        if (heightInches.isNotEmpty()) {
            totalInches = totalInches.plus(heightInches.toInt())
        }

        return totalInches
    }

    override fun getFeetFromInches(inches: Int): Int = inches.div(12)

    override fun getRemainingInches(inches: Int): Int = inches.rem(12)

    private fun showLoading() {
        if (isViewAttached) {
            view?.doAsync {
                uiThread {
                    it.showLoading()
                }
            }
        }
    }

    private fun hideLoading() {
        if (isViewAttached) {
            view?.doAsync {
                uiThread {
                    it.hideLoading()
                }
            }
        }
    }

    override fun dispose() {
        compositeDisposable.clear()
    }
}