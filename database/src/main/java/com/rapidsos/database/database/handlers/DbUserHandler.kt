package com.rapidsos.database.database.handlers

import com.rapidsos.database.database.EraDB
import com.rapidsos.emergencydatasdk.data.user.User
import io.reactivex.Maybe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.doAsync

/**
 * @author Josias Sena
 */
class DbUserHandler(database: EraDB) {

    private val userDao = database.userDao()

    /**
     * Inserts a current user into the database.
     * There should only ever be one user in the database.
     *
     * @param user The user to insert
     */
    fun insertUser(user: User) {
        doAsync {
            userDao.insert(user)
        }
    }

    /**
     * @return the current user in the database
     */
    fun getCurrentUser(): Maybe<User> {
        return userDao.getUser()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }
}