package com.rapidsos.era.main.view

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.support.design.widget.BottomNavigationView
import android.view.Menu
import android.view.MenuItem
import com.hannesdorfmann.mosby3.mvp.MvpActivity
import com.rapidsos.era.R
import com.rapidsos.era.application.App
import com.rapidsos.era.connected_devices.view.ConnectedDevicesFragment
import com.rapidsos.era.helpers.log_out.LogOutDialog
import com.rapidsos.era.main.presenter.MainPresenterImpl
import com.rapidsos.era.midas.service.WearableService
import com.rapidsos.era.profile.service.ProfileService
import com.rapidsos.era.profile.view.ProfileFragment
import com.rapidsos.era.settings.view.SettingsActivity
import com.rapidsos.shared.notification.PanicNotificationHandler
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.startActivity
import javax.inject.Inject

class MainActivity : MvpActivity<MainView, MainPresenterImpl>(), MainView,
        BottomNavigationView.OnNavigationItemSelectedListener, AnkoLogger {

    private val alarmManager by lazy { getSystemService(Context.ALARM_SERVICE) as AlarmManager }
    private val logOutDialog by lazy { LogOutDialog(this) }

    @Inject
    lateinit var panicNotificationHandler: PanicNotificationHandler

    @Inject
    lateinit var presenterImpl: MainPresenterImpl

    companion object {
        fun start(context: Context) {
            val starter = Intent(context, MainActivity::class.java)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            context.startActivity(starter)
        }
    }

    override fun createPresenter() = presenterImpl

    override fun onCreate(savedInstanceState: Bundle?) {
        App.component.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)

        initBottomNavigation()

        startWearableService()

        scheduleProfilePollingService(AlarmManager.INTERVAL_DAY)

        if (presenter.isLockScreenWidgetEnabled()) {
            panicNotificationHandler.displayNotification()
        }
    }

    override fun onPause() {
        super.onPause()
        logOutDialog.dismiss()
    }

    /**
     * Initialize the [mainBottomNavigation]
     */
    private fun initBottomNavigation() {
        mainBottomNavigation.setOnNavigationItemSelectedListener(this)

        if (intent?.action == ConnectedDevicesFragment.ACTION_CONNECTED_TO_BT) {
            mainBottomNavigation.selectedItemId = R.id.navigation_connected_devices
        } else {
            mainBottomNavigation.selectedItemId = R.id.navigation_profile
        }
    }

    /**
     * Starts the service for wearables
     */
    private fun startWearableService() {
        startService(Intent(this, WearableService::class.java))
    }

    /**
     * Schedule the profile service to be started every [interval] to poll profile information
     *
     * @param interval how often to should this happen
     */
    private fun scheduleProfilePollingService(interval: Long) {
        val intent = Intent(this, ProfileService::class.java)
        val pendingIntent = PendingIntent.getService(this, 0, intent, 0)

        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(),
                interval, pendingIntent)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main_options, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        item?.let {
            when (it.itemId) {
                R.id.options_settings -> startActivity<SettingsActivity>()
                R.id.options_logout -> logOutDialog.show()
            }
        }

        return true
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.navigation_profile -> {
                tvToolbarTitle.text = getString(R.string.profile)

                supportFragmentManager.beginTransaction()
                        .replace(R.id.mainContent, ProfileFragment())
                        .commit()
                return true
            }
            R.id.navigation_connected_devices -> {
                tvToolbarTitle.text = getString(R.string.connected_devices)

                supportFragmentManager.beginTransaction()
                        .replace(R.id.mainContent, ConnectedDevicesFragment())
                        .commit()
                return true
            }
        }

        return false
    }
}
