package com.rapidsos.era.authentication.login.view

import com.hannesdorfmann.mosby3.mvp.MvpView
import com.rapidsos.era.helpers.interfaces.IError
import com.rapidsos.era.helpers.interfaces.ILoading

/**
 * @author Josias Sena
 */
interface LoginView : MvpView, ILoading, IError {

    /**
     * Called when the user has successfully been logged in.
     */
    fun onLoggedInSuccessfully()

}