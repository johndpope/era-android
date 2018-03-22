package com.rapidsos.era.authentication.login.presenter

import com.hannesdorfmann.mosby3.mvp.MvpPresenter
import com.rapidsos.era.authentication.login.view.LoginView

/**
 * @author Josias Sena
 */
interface LoginPresenter : MvpPresenter<LoginView> {

    /**
     * Logs the user in
     *
     * @param username The username to use when login in
     * @param password The password associated with the username
     */
    fun logIn(username: String, password: String)
}