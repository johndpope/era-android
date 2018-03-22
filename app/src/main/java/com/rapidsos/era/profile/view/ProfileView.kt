package com.rapidsos.era.profile.view

import com.hannesdorfmann.mosby3.mvp.MvpView
import com.rapidsos.emergencydatasdk.data.profile.Profile

/**
 * @author Josias Sena
 */
interface ProfileView : MvpView {

    /**
     * Do something with the profile data received, such as displaying it.
     *
     * @param profile the current users profile information
     */
    fun onGotProfileData(profile: Profile)
}