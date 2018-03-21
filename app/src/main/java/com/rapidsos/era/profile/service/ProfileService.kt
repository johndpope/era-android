package com.rapidsos.era.profile.service

import android.app.Service
import android.content.Intent
import android.util.Log
import com.rapidsos.database.database.handlers.DbProfileHandler
import com.rapidsos.database.database.handlers.DbSessionTokenHandler
import com.rapidsos.database.database.handlers.DbUserHandler
import com.rapidsos.emergencydatasdk.auth.login.SessionManager
import com.rapidsos.emergencydatasdk.data.profile.Profile
import com.rapidsos.emergencydatasdk.internal.helpers.network.SessionTokenVerifier
import com.rapidsos.emergencydatasdk.profile.ProfileProvider
import com.rapidsos.era.application.App
import com.rapidsos.utils.preferences.EraPreferences
import io.reactivex.MaybeObserver
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.error
import javax.inject.Inject

class ProfileService : Service(), AnkoLogger {

    private val compositeDisposable = CompositeDisposable()
    private val personalInfoProvider = ProfileProvider()
    private val sessionManager = SessionManager()

    @Inject
    lateinit var dbUserHandler: DbUserHandler

    @Inject
    lateinit var dbSessionTokenHandler: DbSessionTokenHandler

    @Inject
    lateinit var dbProfileHandler: DbProfileHandler

    @Inject
    lateinit var preferences: EraPreferences

    companion object {
        private const val TAG = "ProfileService"
    }

    override fun onBind(intent: Intent) = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int) = START_NOT_STICKY

    override fun onCreate() {
        super.onCreate()
        App.component.inject(this)

        if (preferences.isLoggedIn() && preferences.isPinValidated()) {
            getCurrentProfileFromBackend()
        } else {
            error("Please have the user log in first, before fetching profile information")
        }
    }

    /**
     * Fetch information from the backend
     */
    private fun getCurrentProfileFromBackend() {
        Log.d(TAG, "getCurrentProfileFromBackend() called.")

        dbSessionTokenHandler.getSessionToken().subscribe { token ->
            if (!SessionTokenVerifier.isTokenExpired(token)) {
                personalInfoProvider.getPersonalInfo(token).subscribe(profileObserver())
            } else {
                dbUserHandler.getCurrentUser().subscribe({ user ->

                    val username = user.username.toString()
                    val password = user.password.toString()

                    if (!username.isEmpty() && !password.isEmpty()) {
                        sessionManager.getSessionToken(username, password)
                                .flatMap {
                                    dbSessionTokenHandler.insertSessionToken(it)
                                    personalInfoProvider.getPersonalInfo(it)
                                }
                                .subscribe(profileObserver())
                    } else {
                        error("The username: $username or password: $password seem to be empty")
                    }
                })
            }
        }
    }

    private fun profileObserver(): MaybeObserver<Profile?> {
        return object : MaybeObserver<Profile?> {
            override fun onSubscribe(disposable: Disposable) {
                compositeDisposable.add(disposable)
            }

            override fun onError(throwable: Throwable) {
                error("Error updating current profile", throwable)
            }

            override fun onSuccess(profile: Profile) {
                Log.d(TAG, "Got profile from backend, updating database.")
                dbProfileHandler.updateProfile(profile)
            }

            override fun onComplete() {
                stopSelf()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }
}