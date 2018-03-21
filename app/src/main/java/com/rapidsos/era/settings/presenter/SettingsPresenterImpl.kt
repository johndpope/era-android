package com.rapidsos.era.settings.presenter

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter
import com.rapidsos.era.BuildConfig
import com.rapidsos.era.settings.view.SettingsView
import com.rapidsos.utils.preferences.EraPreferences
import javax.inject.Inject

/**
 * @author Josias Sena
 */
class SettingsPresenterImpl @Inject constructor(private val preferences: EraPreferences)
    : MvpBasePresenter<SettingsView>(), SettingsPresenter {

    override fun getAppVersion() = "v${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"

    override fun setEnableLockScreenWidget(checked: Boolean) =
            preferences.setEnableLockScreenWidget(checked)

    override fun getIsLockScreenWidgetEnabled() = preferences.isLockScreenWidgetEnabled()
}