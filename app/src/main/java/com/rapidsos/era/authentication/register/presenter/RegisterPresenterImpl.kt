package com.rapidsos.era.authentication.register.presenter

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter
import com.rapidsos.database.database.handlers.DbSessionTokenHandler
import com.rapidsos.database.database.handlers.DbUserHandler
import com.rapidsos.emergencydatasdk.auth.login.SessionManager
import com.rapidsos.emergencydatasdk.auth.register.RegistrationController
import com.rapidsos.emergencydatasdk.data.network_response.SessionToken
import com.rapidsos.emergencydatasdk.data.user.User
import com.rapidsos.era.application.App
import com.rapidsos.era.authentication.register.view.RegisterView
import com.rapidsos.utils.preferences.EraPreferences
import com.rapidsos.utils.utils.Utils
import io.reactivex.MaybeObserver
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.error
import javax.inject.Inject

/**
 * @author Josias Sena
 */
class RegisterPresenterImpl : MvpBasePresenter<RegisterView>(), RegisterPresenter, AnkoLogger {

    private val registrationController = RegistrationController()
    private val loginController = SessionManager()
    private val compositeDisposable = CompositeDisposable()

    @Inject
    lateinit var utils: Utils

    @Inject
    lateinit var preferences: EraPreferences

    @Inject
    lateinit var dbUserHandler: DbUserHandler

    @Inject
    lateinit var dbSessionTokenHandler: DbSessionTokenHandler

    init {
        App.component.inject(this)
    }

    /**
     * Register a brand new user
     *
     * @param user the user to register
     */
    override fun register(user: User) {
        registrationController.register(user).subscribe(object : MaybeObserver<User?> {

            override fun onSubscribe(d: Disposable) {
                showLoading()
                compositeDisposable.add(d)
            }

            override fun onError(throwable: Throwable) {
                hideLoading()

                throwable.message?.let {
                    error("Register error: $it")
                    showError(it)
                }
            }

            override fun onSuccess(userReturned: User) {
                preferences.setIsLoggedIn(true)

                dbUserHandler.insertUser(user)

                loginUser(user)
            }

            override fun onComplete() = Unit
        })
    }

    private fun loginUser(user: User) {
        loginController.getSessionToken(user.username as String, user.password as String)
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

                    override fun onSuccess(sessionToken: SessionToken) {
                        preferences.setIsLoggedIn(true)

                        dbSessionTokenHandler.insertSessionToken(sessionToken)

                        if (isViewAttached) {
                            view?.onRegisteredSuccessfully()
                        }
                    }

                    override fun onComplete() = hideLoading()
                })
    }

    private fun showError(error: String) {
        if (isViewAttached) {
            view?.showError(error)
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

    /**
     * Un-subscribe the presenter and all of the subscriptions in it
     */
    override fun unSubscribe() = compositeDisposable.clear()

}
