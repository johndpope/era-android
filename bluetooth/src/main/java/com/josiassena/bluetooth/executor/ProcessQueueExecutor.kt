package com.josiassena.bluetooth.executor

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.util.Log
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.error
import java.util.*

/**
 * This class is used to execute the read, write and write descriptor request one by one in a 1.1
 * second delay.
 */
class ProcessQueueExecutor : Thread(), AnkoLogger {

    private val processQueueTimer = Timer()

    companion object {

        private const val TAG = "ProcessQueueExecutor"
        private const val EXECUTE_DELAY: Long = 1100 // delay in execution

        const val REQUEST_TYPE_READ_CHAR = 1
        const val REQUEST_TYPE_WRITE_CHAR = 2
        const val REQUEST_TYPE_WRITE_DESCRIPTOR = 3

        private val processList = ArrayList<ReadWriteCharacteristic>()

        /**
         * Adds the request to ProcessQueueExecutor
         *
         * @param readWriteCharacteristic - characteristic to add to thw queue
         */
        fun addProcess(readWriteCharacteristic: ReadWriteCharacteristic) {
            processList.add(readWriteCharacteristic)
        }

        /**
         * Removes the request from ProcessQueueExecutor
         *
         * @param readWriteCharacteristic - characteristic to remove from the queue
         */
        private fun removeProcess(readWriteCharacteristic: ReadWriteCharacteristic) {
            processList.remove(readWriteCharacteristic)
        }
    }

    private fun executeProcess() {

        if (!processList.isEmpty()) {

            val readWriteCharacteristic = processList[0]
            val type = readWriteCharacteristic.requestType

            val bluetoothGatt = readWriteCharacteristic.bluetoothGatt
            val parseObject = readWriteCharacteristic.anyObject

            when (type) {
                REQUEST_TYPE_READ_CHAR -> handleReadCharacteristicRequest(bluetoothGatt,
                        parseObject as BluetoothGattCharacteristic?)
                REQUEST_TYPE_WRITE_CHAR -> handleWriteCharacteristicRequest(bluetoothGatt,
                        parseObject as BluetoothGattCharacteristic?)
                REQUEST_TYPE_WRITE_DESCRIPTOR -> handleWriteDescriptorRequest(bluetoothGatt,
                        parseObject as BluetoothGattDescriptor?)
                else -> {
                    error("Invalid type")
                }
            }

            removeProcess(readWriteCharacteristic)
        }
    }

    private fun handleWriteDescriptorRequest(bluetoothGatt: BluetoothGatt?,
                                             parseObject: BluetoothGattDescriptor?) {
        try {
            bluetoothGatt?.writeDescriptor(parseObject)
        } catch (e: RuntimeException) {
            Log.e(TAG, "executeProcess: " + e.message, e)
        }
    }

    private fun handleWriteCharacteristicRequest(bluetoothGatt: BluetoothGatt?,
                                                 parseObject: BluetoothGattCharacteristic?) {
        try {
            bluetoothGatt?.writeCharacteristic(parseObject)
        } catch (e: RuntimeException) {
            Log.e(TAG, "executeProcess: " + e.message, e)
        }
    }

    private fun handleReadCharacteristicRequest(bluetoothGatt: BluetoothGatt?,
                                                parseObject: BluetoothGattCharacteristic?) {
        try {
            bluetoothGatt?.readCharacteristic(parseObject)
        } catch (e: RuntimeException) {
            Log.e(TAG, "executeProcess: " + e.message, e)
        }
    }

    override fun interrupt() {
        super.interrupt()
        processQueueTimer.cancel()
    }

    override fun run() {
        super.run()
        processQueueTimer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                executeProcess()
            }
        }, 0, EXECUTE_DELAY)
    }
}
