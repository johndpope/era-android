package com.rapidsos.era.splash_screen.presenter

import com.hannesdorfmann.mosby3.mvp.MvpPresenter
import com.rapidsos.era.splash_screen.view.SplashView

/**
 * @author Josias Sena
 */
interface SplashScreenPresenter : MvpPresenter<SplashView> {

    /**
     * Take the user to the next screen. Whatever that screen may be.
     */
    fun goToNextScreen()

    /**
     * Unsubscribe the presenter and all of the subscriptions in it
     */
    fun unSubscribe()
}
