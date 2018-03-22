package com.rapidsos.midas.fail_safe

import android.content.Context
import com.rapidsos.utils.utils.Utils
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.error
import java.util.concurrent.TimeUnit

/**
 * Internal Midas fail safe timer. Triggered when a midas flow is triggered.
 * If the flow fails then the native dialer is opened instead.
 *
 * @author Josias Sena
 */
object FailSafeTimer : AnkoLogger {

    private val timer: Single<Long> by lazy {
        Single.timer(2, TimeUnit.MINUTES)
                .observeOn(AndroidSchedulers.mainThread())
    }

    private var disposable: Disposable? = null

    /**
     * Start the Midas timer. If the end is reached, native dialer is opened to dial 911
     *
     * @param context the current context
     */
    fun start(context: Context) {
        val utils = Utils(context)

        disposable = timer.subscribe({ _ ->
            error("Never got a 'Cancel fail safe' push. Using native dialer.")

            utils.dialPhoneNumber("911")

            stop()
        }, { t: Throwable ->
            error("An error occurred with the fail safe timer! Using native dialer", t)

            utils.dialPhoneNumber("911")

            stop()
        })
    }

    /**
     * Stop the timer
     */
    fun stop() {
        disposable?.dispose()
    }
}