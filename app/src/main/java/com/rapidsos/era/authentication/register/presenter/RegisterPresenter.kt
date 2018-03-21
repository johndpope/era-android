package com.rapidsos.era.authentication.register.presenter

import com.hannesdorfmann.mosby3.mvp.MvpPresenter
import com.rapidsos.emergencydatasdk.data.user.User
import com.rapidsos.era.authentication.register.view.RegisterView

/**
 * @author Josias Sena
 */
interface RegisterPresenter : MvpPresenter<RegisterView> {

    /**
     * Register a brand new user
     *
     * @param user the user to register
     */
    fun register(user: User)

    /**
     * Un-subscribe the presenter and all of the subscriptions in it
     */
    fun unSubscribe()
}