package com.rapidsos.era.splash_screen.view

import com.hannesdorfmann.mosby3.mvp.MvpView

/**
 * @author Josias Sena
 */
interface SplashView : MvpView {

    /**
     * Take the user to the main app screen
     */
    fun goToMainScreen()

    /**
     * Take the user to the login screen
     */
    fun goToLoginScreen()

    /**
     * Take the user to the phone number screen
     */
    fun goToPhoneNumberScreen()
}