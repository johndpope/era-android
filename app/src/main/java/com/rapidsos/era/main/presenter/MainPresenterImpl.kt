package com.rapidsos.era.main.presenter

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter
import com.rapidsos.era.main.view.MainView
import com.rapidsos.utils.preferences.EraPreferences
import org.jetbrains.anko.AnkoLogger
import javax.inject.Inject

/**
 * @author Josias Sena
 */
class MainPresenterImpl @Inject constructor(private val preferences: EraPreferences) :
        MvpBasePresenter<MainView>(), MainPresenter, AnkoLogger {

    init {

    }

    override fun isLockScreenWidgetEnabled(): Boolean = preferences.isLockScreenWidgetEnabled()
}