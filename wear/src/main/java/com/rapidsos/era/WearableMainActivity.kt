package com.rapidsos.era

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.wearable.activity.WearableActivity
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.wearable.MessageApi
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Wearable
import com.rapidsos.utils.utils.Utils
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.error
import org.jetbrains.anko.longToast

class WearableMainActivity : WearableActivity(), AnkoLogger, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, MessageApi.MessageListener {

    private val panicHandler: WearablePanicHandler by lazy {
        WearablePanicHandler(Wearable.NodeApi, googleApiClient)
    }

    private val confirmationDialog: AlertDialog by lazy {
        AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle(getString(R.string.confirmation))
                .setMessage(getString(R.string.confirm_calling_911))
                .setPositiveButton(R.string.yes, { _, _ ->
                    longToast(getString(R.string.init_please_wait))
                    panicHandler.panic()
                    finish()
                })
                .setNegativeButton(getString(R.string.no), { _, _ ->
                }).create()
    }

    private lateinit var googleApiClient: GoogleApiClient

    private lateinit var utils: Utils

    companion object {
        private const val PERMISSION_REQUEST_CODE = 158
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setAmbientEnabled()

        initGoogleApiClient()

        utils = Utils(this)

        btnPanic.setOnClickListener { startPanicEvent() }
    }

    private fun initGoogleApiClient() {
        googleApiClient = GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Wearable.API)
                .useDefaultAccount()
                .build()
    }

    private fun startPanicEvent() {
        val locationPermission = Manifest.permission.ACCESS_FINE_LOCATION

        if (ContextCompat.checkSelfPermission(this, locationPermission) !=
                PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(locationPermission), PERMISSION_REQUEST_CODE)
        } else {
            confirmationDialog.show()
        }
    }

    override fun onStart() {
        super.onStart()
        if (!googleApiClient.isConnected) {
            googleApiClient.connect()
        }
    }

    override fun onStop() {
        if (googleApiClient.isConnected) {
            Wearable.MessageApi.removeListener(googleApiClient, this)
            googleApiClient.disconnect()
        }
        super.onStop()
    }

    override fun onMessageReceived(messageEvent: MessageEvent?) = Unit // Do nothing

    override fun onConnected(p0: Bundle?) {
        Wearable.MessageApi.addListener(googleApiClient, this)
    }

    override fun onConnectionSuspended(p0: Int) {
        error("onConnectionSuspended() called with: i = [$p0]")
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        error("onConnectionFailed() called with: connectionResult = [$connectionResult]")
    }
}
