package com.rapidsos.shared

import android.app.IntentService
import android.content.Intent
import com.rapidsos.shared.midas_confirmation_activity.view.ConfirmationActivity
import com.rapidsos.utils.utils.Utils
import org.jetbrains.anko.AnkoLogger

/**
 * @author josiassena
 */
class PanicIntentService : IntentService(TAG), AnkoLogger {

    private val utils: Utils by lazy { Utils(this) }

    companion object {
        private const val TAG = "PanicIntentService"

        const val ACTION_PANIC_WITH_CONFIRMATION = "create_panic_alert_with_confirmation"
        const val ACTION_INSTANT_PANIC = "create_instant_panic_alert"
        const val ACTION_OPEN_NATIVE_DIALER = "open_native_dialer"
    }

    /**
     * This method is invoked on the worker thread with a request to process.
     * Only one Intent is processed at a time, but the processing happens on a
     * worker thread that runs independently from other application logic.
     * So, if this code takes a long time, it will hold up other requests to
     * the same IntentService, but it will not hold up anything else.
     * When all requests have been handled, the IntentService stops itself,
     * so you should not call [stopSelf].
     *
     * @param intent The value passed to [Context.startService].
     *               This may be null if the service is being restarted after
     *               its process has gone away; see [Service.onStartCommand]
     *               for details.
     */
    override fun onHandleIntent(intent: Intent?) {
        if (intent != null) {
            val action = intent.action

            val confirmationActivityIntent = Intent(this, ConfirmationActivity::class.java)

            when (action) {
                ACTION_PANIC_WITH_CONFIRMATION -> {
                    dismissNotificationDrawer()
                    confirmationActivityIntent.putExtra(ACTION_INSTANT_PANIC, false)
                    startActivity(confirmationActivityIntent)
                }
                ACTION_INSTANT_PANIC -> {
                    confirmationActivityIntent.putExtra(ACTION_INSTANT_PANIC, true)
                    startActivity(confirmationActivityIntent)
                }
                ACTION_OPEN_NATIVE_DIALER -> {
                    utils.dialPhoneNumber("911")
                }
            }
        }
    }

    /**
     * Dismiss the devices notification drawer
     */
    private fun dismissNotificationDrawer() = sendBroadcast(Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS))
}