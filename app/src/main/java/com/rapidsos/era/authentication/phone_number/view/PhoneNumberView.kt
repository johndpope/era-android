package com.rapidsos.era.authentication.phone_number.view

import com.hannesdorfmann.mosby3.mvp.MvpView
import com.rapidsos.era.helpers.interfaces.IError
import com.rapidsos.era.helpers.interfaces.ILoading

/**
 * @author Josias Sena
 */
interface PhoneNumberView : MvpView, ILoading, IError {

    /**
     * Take the user to the main app screen
     */
    fun goToMainScreen()

    /**
     * Display the views to enter/validate a pin number
     */
    fun showPinViews()

}
