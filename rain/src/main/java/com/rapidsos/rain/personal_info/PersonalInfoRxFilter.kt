package com.rapidsos.rain.personal_info

import com.rapidsos.rain.data.profile.Profile
import io.reactivex.Observer
import retrofit2.Response

/**
 * @author Josias Sena
 */
internal class PersonalInfoRxFilter {

    companion object {
        internal fun filter(response: Response<Profile>, observer: Observer<Profile>): Boolean {
            return if (response.isSuccessful) {
                true
            } else {
                val message = response.message()

                observer.onError(Throwable(message))

                false
            }
        }
    }

}