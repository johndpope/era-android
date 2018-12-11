package com.rapidsos.shared.midas_confirmation_activity.view

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AlertDialog
import com.hannesdorfmann.mosby3.mvp.MvpActivity
import com.rapidsos.database.database.EraDB
import com.rapidsos.database.database.handlers.DbProfileHandler
import com.rapidsos.midas.flow.MidasFLow
import com.rapidsos.shared.PanicIntentService
import com.rapidsos.shared.R
import com.rapidsos.shared.midas_confirmation_activity.presenter.ConfirmationPresenterImpl
import com.rapidsos.shared.notification.PanicNotificationHandler
import com.rapidsos.utils.preferences.EraPreferences
import com.rapidsos.utils.utils.Utils
import org.jetbrains.anko.AnkoLogger
import pl.charmas.android.reactivelocation2.ReactiveLocationProvider

class ConfirmationActivity : MvpActivity<ConfirmationView, ConfirmationPresenterImpl>(),
        ConfirmationView, AnkoLogger {

    private val confirmationDialog: AlertDialog by lazy {
        AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle(getString(R.string.confirmation))
                .setMessage(getString(R.string.confirm_calling_911))
                .setPositiveButton(R.string.yes, { _, _ ->
                    trigger()
                    finish()
                })
                .setNegativeButton(R.string.no, { _, _ ->
                    finish()
                }).create()
    }

    private val confirmationPresenterImpl: ConfirmationPresenterImpl by lazy(LazyThreadSafetyMode.PUBLICATION) {
        ConfirmationPresenterImpl(dbProfileHandler, locationProvider, panicNotificationHandler, pendingIntent)
    }

    private val utils: Utils by lazy(LazyThreadSafetyMode.PUBLICATION) {
        Utils(this)
    }

    private val dbProfileHandler: DbProfileHandler by lazy(LazyThreadSafetyMode.PUBLICATION) {
        val database = EraDB.getDatabase(this)
        DbProfileHandler(database)
    }

    private val midasFLow: MidasFLow by lazy(LazyThreadSafetyMode.PUBLICATION) {
        MidasFLow(this)
    }

    private val locationProvider: ReactiveLocationProvider by lazy(LazyThreadSafetyMode.PUBLICATION) {
        ReactiveLocationProvider(this)
    }

    private val panicNotificationHandler: PanicNotificationHandler by lazy(LazyThreadSafetyMode.PUBLICATION) {
        PanicNotificationHandler(this)
    }

    private val pendingIntent: PendingIntent by lazy(LazyThreadSafetyMode.PUBLICATION) {
        val panicIntent = Intent(this, PanicIntentService::class.java).apply {
            action = PanicIntentService.ACTION_OPEN_NATIVE_DIALER
        }

        PendingIntent.getService(this, 0, panicIntent, 0)
    }

    private lateinit var preferences: EraPreferences

    override fun createPresenter() = confirmationPresenterImpl

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferences = EraPreferences(this)
    }

    override fun onStart() {
        super.onStart()

        if (intent.hasExtra(PanicIntentService.ACTION_INSTANT_PANIC)) {
            val isInstant = intent.getBooleanExtra(PanicIntentService.ACTION_INSTANT_PANIC, false)

            if (isInstant) {
                trigger()
            } else {
                confirmationDialog.show()
            }
        }
    }

    /**
     * Trigger a midas call flow
     */
    private fun trigger() {
        when {
            !preferences.isLoggedIn() || !preferences.isPinValidated() -> {
                presenter.showMessage("You must be logged in with a validated phone number.")
            }
            !isLocationPermissionEnabled() -> {
                displayLocationPermRequiredError()
            }
            !utils.isGpsEnabled() -> {
                displayGpsRequiredError()
            }
            else -> {
                presenter.triggerMidasCallFlow(midasFLow)
            }
        }
    }

    private fun isLocationPermissionEnabled(): Boolean {
        return ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    override fun onPause() {
        super.onPause()
        confirmationDialog.dismiss()
    }

    private fun displayGpsRequiredError() =
            presenter.showErrorMessage(getString(R.string.gps_required_native_dialer))

    private fun displayLocationPermRequiredError() =
            presenter.showErrorMessage(getString(R.string.requires_location_perm_native_dialer))
}