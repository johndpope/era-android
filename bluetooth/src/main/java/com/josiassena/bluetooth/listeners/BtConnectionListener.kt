package com.josiassena.bluetooth.listeners

import android.bluetooth.BluetoothDevice

/**
 * @author Josias Sena
 */
interface BtConnectionListener {

    /**
     * Called when a successful bluetooth connection has been made
     *
     * @param bluetoothDevice the device that was just connected
     */
    fun onConnected(bluetoothDevice: BluetoothDevice?)

    /**
     * Called when a successful bluetooth disconnection has been made
     */
    fun onDisconnected()

    /**
     * Called when an error occurred while trying to connect
     *
     * @param throwable the error thrown
     */
    fun onError(throwable: Throwable)

}