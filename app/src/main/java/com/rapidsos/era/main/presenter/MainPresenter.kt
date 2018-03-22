package com.rapidsos.era.main.presenter

import com.hannesdorfmann.mosby3.mvp.MvpPresenter
import com.rapidsos.era.main.view.MainView

/**
 * @author Josias Sena
 */
interface MainPresenter : MvpPresenter<MainView> {

    /**
     * @return returns true if the lock screen widget is enabled, false otherwise
     */
    fun isLockScreenWidgetEnabled(): Boolean

}