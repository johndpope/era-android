package com.rapidsos.utils.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.support.v4.content.ContextCompat
import android.view.View
import android.view.Window
import android.view.inputmethod.InputMethodManager
import com.rapidsos.utils.constants.permissionsList
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit

/**
 * @author Josias Sena
 */
class Utils(private val context: Context) {

    /**
     * Hides the soft keyboard
     */
    fun hideKeyboard(view: View?) {
        val inputMethodManager = view?.context
                ?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    /**
     * Hides the app status bar
     */
    fun hideStatusBar(window: Window) {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
    }

    /**
     * Hides the app status bar. If the status bar is displayed again, hide it after a certain delay.
     */
    fun hideStatusBarWithDelay(window: Window, delay: Long) {
        val decorView = window.decorView
        val uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN
        decorView.systemUiVisibility = uiOptions

        decorView.setOnSystemUiVisibilityChangeListener { visibility ->
            if (visibility and View.SYSTEM_UI_FLAG_FULLSCREEN == 0) {
                Observable.timer(delay, TimeUnit.MILLISECONDS)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe {
                            decorView.systemUiVisibility = uiOptions
                        }
            }
        }
    }


    /**
     * Checks if all the permissions in the [permissionsList] have been granted
     *
     * @return true if all permissions are granted, false otherwise
     */
    fun isAllPermissionsGranted(): Boolean =
            permissionsList.none {
                ContextCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED
            }

    /**
     * Checks if the gps is enabled
     *
     * @return true if the gps is enabled, false otherwise
     */
    fun isGpsEnabled(): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    /**
     * Open up the native dialer and prepopulate it with a phone number
     *
     * @param phoneNumber the hone number to dial
     */
    fun dialPhoneNumber(phoneNumber: String) {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:$phoneNumber")
        context.startActivity(intent)
    }

}