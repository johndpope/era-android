package com.rapidsos.era.authentication.register.view

import com.hannesdorfmann.mosby3.mvp.MvpView
import com.rapidsos.era.helpers.interfaces.IError
import com.rapidsos.era.helpers.interfaces.ILoading

/**
 * @author Josias Sena
 */
interface RegisterView : MvpView, ILoading, IError {

    /**
     * Called when a user has been registered successfully
     */
    fun onRegisteredSuccessfully()
}