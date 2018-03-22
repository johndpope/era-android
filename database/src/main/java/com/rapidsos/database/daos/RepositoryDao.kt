package com.rapidsos.database.daos

import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Update

/**
 * The repository data access object. Holds the global methods used in most DAOs
 *
 * @author Josias Sena
 */
interface RepositoryDao<in T> {

    /**
     * Insert an object in the database.
     *
     * @param obj the object to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(obj: T)

    /**
     * Update an object from the database.
     *
     * @param obj the object to be updated
     */
    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(obj: T)

}