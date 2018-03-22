package com.rapidsos.database.daos

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Query
import com.rapidsos.emergencydatasdk.data.profile.Profile
import io.reactivex.Flowable
import io.reactivex.Maybe

/**
 * The Profile data access object
 *
 * @author Josias Sena
 */
@Dao
interface ProfileDao : RepositoryDao<Profile> {

    /**
     * Automatically notifies all subscribers when changes have been made to the database.
     *
     * @return the current database profile
     */
    @Query("SELECT * FROM profile LIMIT 1")
    fun getProfileWithUpdates(): Flowable<Profile>

    /**
     * Does not notify subscribers of database changes.
     *
     * @return the current database profile
     */
    @Query("SELECT * FROM profile LIMIT 1")
    fun getProfileWithoutUpdates(): Maybe<Profile>

    /**
     * Delete the entire table
     */
    @Query("DELETE FROM profile")
    fun deleteAll()

}