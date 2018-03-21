package com.rapidsos.era.settings.presenter

import com.hannesdorfmann.mosby3.mvp.MvpPresenter
import com.rapidsos.era.settings.view.SettingsView

/**
 * @author Josias Sena
 */
interface SettingsPresenter : MvpPresenter<SettingsView> {

    /**
     * @return the current app version
     */
    fun getAppVersion(): String

    /**
     * Enable lock screen widget
     * @param checked if true lock screen is enabled, if false it is not
     */
    fun setEnableLockScreenWidget(checked: Boolean)

    /**
     * @return if the lock screen widget is enabled
     */
    fun getIsLockScreenWidgetEnabled(): Boolean
}