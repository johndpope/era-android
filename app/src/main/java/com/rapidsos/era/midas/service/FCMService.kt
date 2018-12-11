package com.rapidsos.era.midas.service

import android.util.Log
import com.google.firebase.messaging.RemoteMessage
import com.rapidsos.beaconsdk.BeaconFCMService
import com.rapidsos.midas.fail_safe.FailSafeTimer
import com.rapidsos.utils.utils.Utils
import org.jetbrains.anko.AnkoLogger

/**
 * @author Josias Sena
 */
class FCMService : BeaconFCMService(), AnkoLogger {

    private val utils: Utils by lazy { Utils(this) }

    companion object {
        private const val TRIGGER_FAIL_SAFE = "com.rapidsos.kronos.failsafe"
        private const val CANCEL_FAIL_SAFE = "com.rapidsos.kronos.emergencyAck"
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val action = remoteMessage.data["action"]

        when (action) {
            TRIGGER_FAIL_SAFE -> {
                Log.d(loggerTag, "Received a push to trigger fail safe")
                utils.dialPhoneNumber("911")
            }
            CANCEL_FAIL_SAFE -> {
                Log.d(loggerTag, "Received a push to cancel the midas timer")
                FailSafeTimer.stop()
            }
        }
    }
}