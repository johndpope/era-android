package com.rapidsos.era.profile.edit_profile.presenter

import com.hannesdorfmann.mosby3.mvp.MvpPresenter
import com.rapidsos.emergencydatasdk.data.profile.Profile
import com.rapidsos.era.profile.edit_profile.view.EditProfileView
import java.io.File

/**
 * @author Josias Sena
 */
interface EditProfilePresenter : MvpPresenter<EditProfileView> {

    /**
     * Get the current users profile information
     */
    fun getProfileInformation()

    /**
     * Save the the profile into the database
     *
     * @param profile the profile to save
     */
    fun saveProfileInfo(profile: Profile)

    /**
     * @param heightFeet the feet to convert to inches
     * @param heightInches the inches to add to the feet inches
     * @return the complete value of the feet in inches plus the inches
     */
    fun getHeightInInches(heightFeet: String, heightInches: String): Int

    /**
     * Example: if 71 inches (which is 5.91667 feet) is provided then 5 is returned
     *
     * @param inches full height in inches
     * @return the feet extracted from the provided inches
     */
    fun getFeetFromInches(inches: Int): Int

    /**
     * Example: if 71 inches (which is 5.91667 feet) is provided then 9 is returned
     *
     * @param inches remaining inches
     * @return the inches remaining after extracting the feet in inches
     */
    fun getRemainingInches(inches: Int): Int

    /**
     * Upload a profile picture, once uploaded update the profile with the profile pictures url
     *
     * @param profile the profile being updated
     * @param profilePicFile the profile picture to upload
     */
    fun uploadProfilePic(profile: Profile, profilePicFile: File)

    /**
     * Dispose all of the disposables in the presenter
     */
    fun dispose()
}