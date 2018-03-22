package com.rapidsos.era.authentication.password_reset.view

import com.hannesdorfmann.mosby3.mvp.MvpView
import com.rapidsos.era.helpers.interfaces.ILoading

/**
 * @author Josias Sena
 */
interface PasswordResetView : MvpView, ILoading {

    /**
     * Display a message to the user from an exception
     * @param message the message to display
     */
    fun showMessage(message: String)

    /**
     * Notify the view that the reset email was successfully sent
     */
    fun onResetEmailSent()
}