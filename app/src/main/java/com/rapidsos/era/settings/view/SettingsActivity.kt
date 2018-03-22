package com.rapidsos.era.settings.view

import android.os.Bundle
import com.hannesdorfmann.mosby3.mvp.MvpActivity
import com.rapidsos.era.R
import com.rapidsos.era.application.App
import com.rapidsos.era.settings.presenter.SettingsPresenterImpl
import com.rapidsos.shared.notification.PanicNotificationHandler
import kotlinx.android.synthetic.main.content_settings.*
import kotlinx.android.synthetic.main.toolbar.*
import javax.inject.Inject

class SettingsActivity : MvpActivity<SettingsView, SettingsPresenterImpl>(), SettingsView {

    @Inject
    lateinit var panicNotificationHandler: PanicNotificationHandler

    @Inject
    lateinit var presenterImpl: SettingsPresenterImpl

    override fun createPresenter() = presenterImpl

    override fun onCreate(savedInstanceState: Bundle?) {
        App.component.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        setSupportActionBar(toolbar)
        tvToolbarTitle.text = getString(R.string.settings)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        switchEnableWidget.setOnCheckedChangeListener { _, isChecked ->
            presenter.setEnableLockScreenWidget(isChecked)

            if (isChecked) {
                panicNotificationHandler.displayNotification()
            } else {
                panicNotificationHandler.dismissNotification()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        tvVersionBuild.text = presenter.getAppVersion()

        switchEnableWidget.isChecked = presenter.getIsLockScreenWidgetEnabled()
    }

}
