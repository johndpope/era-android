package com.rapidsos.database.daos

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Query
import com.rapidsos.emergencydatasdk.data.network_response.SessionToken
import io.reactivex.Maybe

/**
 * The SessionToken data access object
 *
 * @author Josias Sena
 */
@Dao
interface SessionTokenDao : RepositoryDao<SessionToken> {

    /**
     * @return the current session token
     */
    @Query("SELECT * FROM session_token LIMIT 1")
    fun getSessionToken(): Maybe<SessionToken>

    /**
     * Delete the entire table
     */
    @Query("DELETE FROM session_token")
    fun deleteAll()
}