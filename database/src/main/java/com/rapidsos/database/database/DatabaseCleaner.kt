package com.rapidsos.database.database

import org.jetbrains.anko.doAsync

/**
 * @author Josias Sena
 */
class DatabaseCleaner(database: EraDB) {

    private val userDao = database.userDao()
    private val profileDao = database.profileDao()
    private val sessionTokenDao = database.sessionTokenDao()

    /**
     * Delete all the data in the database
     */
    fun deleteAllTables() {
        doAsync {
            userDao.deleteAll()
            profileDao.deleteAll()
            sessionTokenDao.deleteAll()
        }
    }

}