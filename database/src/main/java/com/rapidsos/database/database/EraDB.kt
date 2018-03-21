package com.rapidsos.database.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import android.content.Context
import com.rapidsos.database.daos.ProfileDao
import com.rapidsos.database.daos.SessionTokenDao
import com.rapidsos.database.daos.UserDao
import com.rapidsos.database.database.converter.DatabaseConverter
import com.rapidsos.emergencydatasdk.data.network_response.SessionToken
import com.rapidsos.emergencydatasdk.data.profile.Profile
import com.rapidsos.emergencydatasdk.data.user.User

/**
 * The actual database providing access to all of the DAOs
 *
 * @author Josias Sena
 */
@Database(version = 2, entities = [User::class, Profile::class, SessionToken::class],
        exportSchema = false)
@TypeConverters(DatabaseConverter::class)
abstract class EraDB : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun profileDao(): ProfileDao
    abstract fun sessionTokenDao(): SessionTokenDao

    companion object {
        fun getDatabase(context: Context): EraDB {
            return Room.databaseBuilder(context, EraDB::class.java, "era-db")
                    .fallbackToDestructiveMigration()
                    .build()
        }
    }

}