package com.rapidsos.utils.preferences

import android.content.Context

const val KEY_IS_LOGGED_IN = "is_logged_in"
const val KEY_IS_PIN_VALIDATED = "is_pin_valid"
const val KEY_DEVICE_NUMBER = "key_device_phone_number"
const val KEY_IS_LOCK_SCREEN_WIDGET_ENABLED = "is_lock_widget_enabled"

/**
 * @author Josias Sena
 */
class EraPreferences(context: Context) : KPreferences {

    private var preferences =
            context.getSharedPreferences("era_prefs", Context.MODE_PRIVATE)

    override fun setIsLoggedIn(isLoggedIn: Boolean) =
            preferences.edit().putBoolean(KEY_IS_LOGGED_IN, isLoggedIn).apply()

    override fun isLoggedIn() = preferences.getBoolean(KEY_IS_LOGGED_IN, false)

    override fun getCurrentDevicePhoneNumber(): String =
            preferences.getString(KEY_DEVICE_NUMBER, "")

    override fun setCurrentDevicePhoneNumber(phoneNumber: String) =
            preferences.edit().putString(KEY_DEVICE_NUMBER, phoneNumber).apply()

    override fun setIsPinValidated(isPinValidated: Boolean) =
            preferences.edit().putBoolean(KEY_IS_PIN_VALIDATED, isPinValidated).apply()

    override fun isPinValidated() = preferences.getBoolean(KEY_IS_PIN_VALIDATED, false)

    override fun setEnableLockScreenWidget(checked: Boolean) =
            preferences.edit().putBoolean(KEY_IS_LOCK_SCREEN_WIDGET_ENABLED, checked).apply()

    override fun isLockScreenWidgetEnabled() =
            preferences.getBoolean(KEY_IS_LOCK_SCREEN_WIDGET_ENABLED, false)

    override fun clearAllPreferences() = preferences.edit().clear().apply()
}