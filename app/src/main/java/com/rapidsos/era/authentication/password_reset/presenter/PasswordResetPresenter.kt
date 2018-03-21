package com.rapidsos.era.authentication.password_reset.presenter

import com.hannesdorfmann.mosby3.mvp.MvpPresenter
import com.rapidsos.era.authentication.password_reset.view.PasswordResetView

/**
 * @author Josias Sena
 */
interface PasswordResetPresenter : MvpPresenter<PasswordResetView> {

    /**
     * Reset the user password for the email provided
     * @param email the email to reset the password for
     */
    fun resetPasswordForEmail(email: String)

    /**
     * Dispose all of the disposables in the presenter
     */
    fun dispose()
}