package com.rapidsos.era.main.presenter

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter
import com.rapidsos.androidutils.phone_book.Contact
import com.rapidsos.androidutils.phone_book.PhoneBookManager
import com.rapidsos.beaconsdk.BackendConfiguration
import com.rapidsos.beaconsdk.ConfigurationManager
import com.rapidsos.beaconsdk.OnGotConfigurationsListener
import com.rapidsos.era.main.view.MainView
import com.rapidsos.utils.preferences.EraPreferences
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.error
import javax.inject.Inject

/**
 * @author Josias Sena
 */
class MainPresenterImpl @Inject constructor(private val preferences: EraPreferences,
                                            private val phoneBookManager: PhoneBookManager) :
        MvpBasePresenter<MainView>(), MainPresenter, AnkoLogger {

    private val configurationManager = ConfigurationManager()

    init {
        configurationManager.getConfigurations(preferences.getCurrentDevicePhoneNumber(),
                object : OnGotConfigurationsListener {
                    override fun onSuccess(backendConfiguration: BackendConfiguration?) {
                        val number = backendConfiguration?.smsFallbackNumber.toString()
                        val contact = Contact("RapidSOS Beacon Service", number)
                        phoneBookManager.saveContactToPhoneBook(contact)
                    }

                    override fun onError() {
                        error("Error fetching beacon configuration")
                    }
                })
    }

    override fun isLockScreenWidgetEnabled(): Boolean = preferences.isLockScreenWidgetEnabled()
}