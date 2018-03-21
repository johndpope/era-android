package com.josiassena.bluetooth.data

import android.bluetooth.BluetoothDevice

/**
 * @author Josias Sena
 */
data class BluetoothDeviceRSSI(var bluetoothDevice: BluetoothDevice, var rssi: Int)