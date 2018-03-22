package com.rapidsos.era.connected_devices.view

import android.bluetooth.BluetoothDevice
import com.hannesdorfmann.mosby3.mvp.MvpView
import com.rapidsos.era.helpers.interfaces.IError
import com.rapidsos.era.helpers.interfaces.ILoading

/**
 * @author Josias Sena
 */
interface ConnectedDevicesView : MvpView, ILoading, IError {

    /**
     * Add a bluetooth device to the list of devices displayed to the user
     *
     * @param bluetoothDevice the blue tooth device to be added
     */
    fun addBluetoothDeviceToList(bluetoothDevice: BluetoothDevice)

    /**
     * Display an error to the user if bluetooth is unavailable in the current device.
     */
    fun displayBtUnavailableError()

    /**
     * Enable bluetooth
     */
    fun enableBluetooth()

    /**
     * Notify the user that a connection has been made to the current [bluetoothDevice]
     *
     * @param bluetoothDevice the device that was just connected to
     */
    fun onConnectedToDevice(bluetoothDevice: BluetoothDevice?)

    /**
     * Called when disconnected from a bluetooth device
     */
    fun onDisconnectedFromBluetooth()

}