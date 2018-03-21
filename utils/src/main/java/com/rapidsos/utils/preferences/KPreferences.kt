package com.rapidsos.utils.preferences

/**
 * @author Josias Sena
 */
interface KPreferences {
    fun setIsLoggedIn(isLoggedIn: Boolean)
    fun isLoggedIn(): Boolean
    fun getCurrentDevicePhoneNumber(): String
    fun setCurrentDevicePhoneNumber(phoneNumber: String)
    fun setIsPinValidated(isPinValidated: Boolean)
    fun isPinValidated(): Boolean
    fun setEnableLockScreenWidget(checked: Boolean)
    fun isLockScreenWidgetEnabled(): Boolean
    fun clearAllPreferences()
}