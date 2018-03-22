package com.josiassena.bluetooth.executor

import android.bluetooth.BluetoothGatt

/**
 * Provides details about RequestType, BluetoothGatt object and its Object
 */
data class ReadWriteCharacteristic(var requestType: Int = 0,
                                   var bluetoothGatt: BluetoothGatt?,
                                   var anyObject: Any?)