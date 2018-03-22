package com.rapidsos.era.profile.presenter

import com.hannesdorfmann.mosby3.mvp.MvpPresenter
import com.rapidsos.era.profile.view.ProfileView

/**
 * @author Josias Sena
 */
interface ProfilePresenter : MvpPresenter<ProfileView> {

    /**
     * The the profile for the current user logged in
     */
    fun getCurrentProfile()
}