package com.rapidsos.era.helpers.dependency_injection.modules

import android.content.Context
import com.rapidsos.database.database.DatabaseCleaner
import com.rapidsos.database.database.EraDB
import com.rapidsos.database.database.handlers.DbProfileHandler
import com.rapidsos.database.database.handlers.DbSessionTokenHandler
import com.rapidsos.database.database.handlers.DbUserHandler
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * @author Josias Sena
 */
@Module
class DatabaseModule(val context: Context) {

    @Provides
    @Singleton
    fun providesKronosDB(): EraDB = EraDB.getDatabase(context)

    @Provides
    @Singleton
    fun providesDbSessionTokenHandler(eraDB: EraDB) = DbSessionTokenHandler(eraDB)

    @Provides
    @Singleton
    fun providesDbProfileHandler(eraDB: EraDB): DbProfileHandler = DbProfileHandler(eraDB)

    @Provides
    @Singleton
    fun providesDbUserHandler(eraDB: EraDB): DbUserHandler = DbUserHandler(eraDB)

    @Provides
    @Singleton
    fun providesDatabaseCleaner(eraDB: EraDB): DatabaseCleaner = DatabaseCleaner(eraDB)

}