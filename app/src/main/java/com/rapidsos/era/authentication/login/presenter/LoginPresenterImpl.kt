package com.rapidsos.era.authentication.login.presenter

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter
import com.rapidsos.database.database.handlers.DbSessionTokenHandler
import com.rapidsos.database.database.handlers.DbUserHandler
import com.rapidsos.emergencydatasdk.auth.login.SessionManager
import com.rapidsos.emergencydatasdk.data.network_response.SessionToken
import com.rapidsos.emergencydatasdk.data.user.User
import com.rapidsos.era.application.App
import com.rapidsos.era.authentication.login.view.LoginView
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
class LoginPresenterImpl : MvpBasePresenter<LoginView>(), LoginPresenter, AnkoLogger {

    private val sessionManager = SessionManager()
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
     * Logs the user in
     *
     * @param username The username to use when login in
     * @param password The password associated with the username
     */
    override fun logIn(username: String, password: String) {
        showLoading()

        sessionManager.getSessionToken(username, password)
                .subscribe(object : MaybeObserver<SessionToken?> {

                    override fun onSubscribe(disposable: Disposable) {
                        compositeDisposable.add(disposable)
                    }

                    override fun onError(error: Throwable) {
                        hideLoading()
                        error("Login error", error)

                        if (isViewAttached) {
                            error.message?.let { view?.showError(it) }
                        }
                    }

                    override fun onSuccess(sessionToken: SessionToken) {
                        preferences.setIsLoggedIn(true)

                        dbSessionTokenHandler.insertSessionToken(sessionToken)

                        val currentUser = User().apply {
                            this.username = username
                            this.password = password
                        }

                        dbUserHandler.insertUser(currentUser)

                        if (isViewAttached) {
                            view?.onLoggedInSuccessfully()
                        }
                    }

                    override fun onComplete() = Unit

                })

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