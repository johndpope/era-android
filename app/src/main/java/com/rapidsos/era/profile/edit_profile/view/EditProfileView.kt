package com.rapidsos.era.profile.edit_profile.view

import com.hannesdorfmann.mosby3.mvp.MvpView
import com.rapidsos.emergencydatasdk.data.profile.Profile
import com.rapidsos.era.helpers.interfaces.IError
import com.rapidsos.era.helpers.interfaces.ILoading

/**
 * @author Josias Sena
 */
interface EditProfileView : MvpView, ILoading, IError {

    /**
     * Display the profile information to the user
     */
    fun displayProfile(profile: Profile)

    /**
     * Do something after the profile was saved/updated successfully
     */
    fun onSuccessUpdatingProfile()

}