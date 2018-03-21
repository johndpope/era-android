package com.rapidsos.database.database.handlers

import com.rapidsos.database.database.EraDB
import com.rapidsos.emergencydatasdk.data.profile.Profile
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.doAsync

/**
 * @author Josias Sena
 */
class DbProfileHandler(database: EraDB) {

    private val profileDao = database.profileDao()

    /**
     * Inserts a current profile into the database.
     *
     * @param profile The profile to insert
     */
    fun insertProfile(profile: Profile) {
        doAsync {
            profileDao.insert(profile)
        }
    }

    /**
     * Updates the current profile in the database.
     *
     * @param profile The profile to update
     */
    fun updateProfile(profile: Profile) {
        doAsync {
            profileDao.update(profile)
        }
    }

    /**
     * Automatically notifies all subscribers when changes have been made to the database.
     *
     * @return the current profile in the database
     */
    fun getProfileWithUpdates(): Flowable<Profile> {
        return profileDao.getProfileWithUpdates()
                .subscribeOn(Schedulers.io())
                .distinctUntilChanged()
                .observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * @return the current profile in the database
     */
    fun getProfileWithoutUpdates(): Maybe<Profile> {
        return profileDao.getProfileWithoutUpdates()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

}