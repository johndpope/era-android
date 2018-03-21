package com.rapidsos.rain.personal_info

import com.rapidsos.rain.api.RainApi
import com.rapidsos.rain.connection.ConnectionVerifier
import com.rapidsos.rain.data.network_response.SessionToken
import com.rapidsos.rain.data.profile.Profile
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers

/**
 * @author Josias Sena
 */
class PersonalInfoUpdater(private val api: RainApi) {

    /**
     * Updates a users personal information
     *
     * @param sessionToken the session token required to make the request
     * @param observer the listener that gets notified of an error or success
     */
    fun updatePersonalInfo(sessionToken: SessionToken, profileToUpdate: Profile,
                           observer: Observer<Profile>) {
        ConnectionVerifier.isConnectedToInternet(Consumer { isInternetAvailable ->
            if (isInternetAvailable) {
                api.updatePersonalInfo("Bearer " + sessionToken.accessToken, profileToUpdate)
                        .subscribeOn(Schedulers.io())
                        .filter { response -> PersonalInfoRxFilter.filter(response, observer) }
                        .map { response -> response.body() }
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ profile ->
                            profile?.let {
                                observer.onNext(profile)
                            }

                            observer.onComplete()
                        })
            } else {
                observer.onError(Throwable("Internet connection unavailable."))
            }
        })
    }
}