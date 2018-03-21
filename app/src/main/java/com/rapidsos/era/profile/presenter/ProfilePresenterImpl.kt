package com.rapidsos.era.profile.presenter

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter
import com.rapidsos.database.database.handlers.DbProfileHandler
import com.rapidsos.era.profile.view.ProfileView
import org.jetbrains.anko.AnkoLogger
import javax.inject.Inject

/**
 * @author Josias Sena
 */
class ProfilePresenterImpl @Inject constructor(private val dbProfileHandler: DbProfileHandler) :
        MvpBasePresenter<ProfileView>(), ProfilePresenter, AnkoLogger {

    override fun getCurrentProfile() {
        dbProfileHandler.getProfileWithUpdates().subscribe({ profile ->
            if (isViewAttached) {
                view?.onGotProfileData(profile)
            }
        })
    }
}