package com.rapidsos.database.database.handlers

import com.rapidsos.database.database.EraDB
import com.rapidsos.emergencydatasdk.data.network_response.SessionToken
import io.reactivex.Maybe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.doAsync

/**
 * @author Josias Sena
 */
class DbSessionTokenHandler(database: EraDB) : AnkoLogger {

    private val sessionTokenDao = database.sessionTokenDao()

    fun insertSessionToken(token: SessionToken) {
        doAsync {
            sessionTokenDao.insert(token)
        }
    }

    fun getSessionToken(): Maybe<SessionToken> {
        return sessionTokenDao.getSessionToken()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

}