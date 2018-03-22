package com.rapidsos.era.connected_devices.presenter

import android.app.PendingIntent
import android.bluetooth.BluetoothDevice
import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter
import com.josiassena.bluetooth.BluetoothHandler
import com.josiassena.bluetooth.data.ActionValue
import com.josiassena.bluetooth.data.BluetoothDeviceRSSI
import com.josiassena.bluetooth.listeners.BtConnectionListener
import com.rapidsos.era.connected_devices.view.ConnectedDevicesView
import com.rapidsos.shared.PanicInitiator
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.error
import org.jetbrains.anko.info
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * @author Josias Sena
 */
class ConnectedDevicesPresenterImpl @Inject constructor(private val bluetoothHandler: BluetoothHandler,
                                                        private val panicInitiator: PanicInitiator) :
        MvpBasePresenter<ConnectedDevicesView>(), ConnectedDevicesPresenter, AnkoLogger {

    private val compositeDisposable = CompositeDisposable()

    private var shouldPanic: Boolean = false

    init {
        this.bluetoothHandler.actionListener.subscribe { action: ActionValue ->
            when (action) {
                ActionValue.PRESS -> info("Detected a bluetooth device button press")
                ActionValue.RELEASE -> info("Detected a bluetooth device button release")
                ActionValue.LONG_PRESS -> {
                    info("Detected a bluetooth device long press")

                    if (!shouldPanic) {
                        shouldPanic = true
                    } else {
                        panicInitiator.panicWithConfirmation()
                    }
                }
                ActionValue.FALL -> {
                    panicInitiator.panicWithConfirmation()
                }
            }
        }
    }

    override fun scanForBluetoothDevices() {
        // check if bluetooth is supported on your hardware
        if (!bluetoothHandler.isBluetoothAvailable()) {
            // handle the lack of bluetooth support
            if (isViewAttached) {
                view?.displayBtUnavailableError()
            }
        } else if (!bluetoothHandler.isBluetoothEnabled()) {
            // check if bluetooth is currently enabled and ready for use
            if (isViewAttached) {
                view?.enableBluetooth()
            }
        } else {
            beginScanning()
        }
    }

    private fun beginScanning() {
        showLoading()

        bluetoothHandler.register()

        val disposable = bluetoothHandler.scanForLeDevices()
                .mergeWith(bluetoothHandler.scanForNormalBluetoothDevices())
                .subscribeOn(Schedulers.io())
                .distinct { it.bluetoothDevice.address }
                .timeout(15, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ deviceRSSI: BluetoothDeviceRSSI ->
                    if (isViewAttached) {
                        view?.addBluetoothDeviceToList(deviceRSSI.bluetoothDevice)
                    }
                }, {
                    error("Completed getting bluetooth devices")
                    hideLoading()
                    stopScanningForBluetoothDevices()
                }, {
                    hideLoading()
                })

        compositeDisposable.add(disposable)
    }

    override fun connect(bluetoothDevice: BluetoothDevice, actionIntent: PendingIntent?) {
        stopScanningForBluetoothDevices()

        setConnectionListener(actionIntent)

        bluetoothHandler.connectTo(bluetoothDevice)
    }

    private fun setConnectionListener(actionIntent: PendingIntent?) {
        bluetoothHandler.setConnectionListener(object : BtConnectionListener {
            override fun onConnected(bluetoothDevice: BluetoothDevice?) {

                actionIntent?.let {
                    bluetoothHandler.displayConnectionNotification(it)
                }

                if (isViewAttached) {
                    view?.onConnectedToDevice(bluetoothDevice)
                }
            }

            override fun onDisconnected() {
                bluetoothHandler.dismissConnectionNotification()

                if (isViewAttached) {
                    view?.onDisconnectedFromBluetooth()
                }
            }

            override fun onError(throwable: Throwable) {
                if (isViewAttached) {
                    view.showError(throwable.message.toString())
                }
            }
        })
    }

    override fun disconnect(bluetoothDevice: BluetoothDevice?) {
        shouldPanic = false
        bluetoothHandler.disconnect(bluetoothDevice)
    }

    override fun stopScanningForBluetoothDevices() {
        bluetoothHandler.stopScanningForBluetoothLeDevices()
        bluetoothHandler.stopScanningForNormalBluetoothDevices()
    }

    private fun showLoading() {
        if (isViewAttached) {
            view?.showLoading()
        }
    }

    private fun hideLoading() {
        if (isViewAttached) {
            view?.hideLoading()
        }
    }

    override fun unSubscribe() {
        stopScanningForBluetoothDevices()

        compositeDisposable.clear()
    }

}