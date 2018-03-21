package com.rapidsos.era.connected_devices.presenter

import android.app.PendingIntent
import android.bluetooth.BluetoothDevice
import com.hannesdorfmann.mosby3.mvp.MvpPresenter
import com.rapidsos.era.connected_devices.view.ConnectedDevicesView

/**
 * @author Josias Sena
 */
interface ConnectedDevicesPresenter : MvpPresenter<ConnectedDevicesView> {

    /**
     * Start scanning for bluetooth devices
     */
    fun scanForBluetoothDevices()

    /**
     * Stop scanning for bluetooth devices
     */
    fun stopScanningForBluetoothDevices()

    /**
     * Connect to a [BluetoothDevice]
     *
     * @param bluetoothDevice the device to connect to
     * @param actionIntent intent used to set as the click intent for the "connected to a
     * bluetooth device" dialog
     */
    fun connect(bluetoothDevice: BluetoothDevice, actionIntent: PendingIntent?)

    /**
     * Disconnect from a [BluetoothDevice]
     *
     * @param bluetoothDevice the device to disconnect from
     */
    fun disconnect(bluetoothDevice: BluetoothDevice?)

    /**
     * Un-subscribe all disposables and unregister registered components
     */
    fun unSubscribe()

}