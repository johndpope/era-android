package com.rapidsos.rain.connection

import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.AnkoLogger

/**
 * @author Josias Sena
 */
class ConnectionVerifier : AnkoLogger {

    companion object {
        fun isConnectedToInternet(consumer: Consumer<Boolean>): Disposable? =
                ReactiveNetwork.checkInternetConnectivity()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(consumer)
    }
}