package com.rapidsos.shared.midas_confirmation_activity.presenter

import com.hannesdorfmann.mosby3.mvp.MvpPresenter
import com.rapidsos.midas.flow.MidasFLow
import com.rapidsos.shared.midas_confirmation_activity.view.ConfirmationView

/**
 * @author Josias Sena
 */
interface ConfirmationPresenter : MvpPresenter<ConfirmationView> {

    /**
     * Trigger a midas call flow
     *
     * @param midasFLow the class to use to trigger the call flow
     */
    fun triggerMidasCallFlow(midasFLow: MidasFLow)

    /**
     * Display a message to the user
     *
     * @param message the message to display
     */
    fun showMessage(message: String)

    /**
     * Display an error message to the user
     *
     * @param errorMessage the error message to display
     */
    fun showErrorMessage(errorMessage: String)

    /**
     * Un-subscribe the disposables in the presenter
     */
    fun unSubscribe()
}