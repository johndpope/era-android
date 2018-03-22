package com.rapidsos.era.midas.service

import android.app.Service
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.wearable.MessageApi
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Wearable
import com.google.android.gms.wearable.WearableListenerService
import com.rapidsos.shared.PanicInitiator
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.debug
import org.jetbrains.anko.error
import java.nio.charset.StandardCharsets

/**
 * @author Josias Sena
 */
class WearableService : WearableListenerService(), ResultCallback<MessageApi.SendMessageResult>,
        AnkoLogger, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private val panicInitiator: PanicInitiator by lazy(LazyThreadSafetyMode.PUBLICATION) {
        PanicInitiator(this)
    }

    private lateinit var googleApiClient: GoogleApiClient

    companion object {
        private const val CREATE_ALERT = "create_alert"
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        googleApiClient = GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Wearable.API)
                .useDefaultAccount()
                .build()

        googleApiClient.connect()

        return Service.START_STICKY
    }

    override fun onMessageReceived(messageEvent: MessageEvent?) {
        super.onMessageReceived(messageEvent)

        messageEvent?.let {
            val message = String(messageEvent.data, StandardCharsets.UTF_8)
            Log.d(loggerTag, message)

            if (message == CREATE_ALERT) {
                panicInitiator.panic()
            }
        }
    }

    override fun onDestroy() {
        if (googleApiClient.isConnected) {
            Wearable.MessageApi.removeListener(googleApiClient, this)
            googleApiClient.disconnect()
        }

        super.onDestroy()
    }

    override fun onResult(sendMessageResult: MessageApi.SendMessageResult) {
        debug("onResult() called with: sendMessageResult = [ ${sendMessageResult.status} ]")
    }

    override fun onConnected(p0: Bundle?) {
        Wearable.MessageApi.addListener(googleApiClient, this)
    }

    override fun onConnectionSuspended(p0: Int) =
            error("onConnectionSuspended() called with: i = [$p0]")

    override fun onConnectionFailed(connectionResult: ConnectionResult) =
            error("onConnectionFailed() called with: connectionResult = [$connectionResult]")

}