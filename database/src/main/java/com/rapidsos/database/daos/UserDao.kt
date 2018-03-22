package com.rapidsos.database.daos

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Query
import com.rapidsos.emergencydatasdk.data.user.User
import io.reactivex.Maybe

/**
 * The User data access object
 *
 * @author Josias Sena
 */
@Dao
interface UserDao : RepositoryDao<User> {

    /**
     * @return the current database user
     */
    @Query("SELECT * FROM user LIMIT 1")
    fun getUser(): Maybe<User>

    /**
     * Delete the entire table
     */
    @Query("DELETE FROM user")
    fun deleteAll()

}