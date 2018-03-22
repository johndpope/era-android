package com.josiassena.bluetooth.service

import android.app.PendingIntent
import android.app.Service
import android.bluetooth.*
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.util.Log
import com.josiassena.bluetooth.executor.ProcessQueueExecutor
import com.josiassena.bluetooth.executor.ReadWriteCharacteristic
import com.josiassena.bluetooth.helpers.*
import com.josiassena.bluetooth.notification.BtConnectedNotification
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.debug
import org.jetbrains.anko.error
import org.jetbrains.anko.info
import java.util.*

/**
 * The communication between the Bluetooth Low Energy device will be communicated through this
 * service class only The initial connect request and disconnect request will be executed in this
 * class.Also, all the status from the Bluetooth device will be notified in the corresponding
 * callback methods.
 *
 * @author Josias Sena
 */
class BluetoothLeService : Service(), AnkoLogger {

    val bluetoothGattMap: HashMap<String, BluetoothGatt> = hashMapOf()

    private var processQueueExecutor = ProcessQueueExecutor()
    private val binder = LocalBinder()

    private val bluetoothManager by lazy {
        getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    }

    private val btConnectedNotification by lazy {
        BtConnectedNotification(this)
    }

    /**
     * The connection status of the Blue tooth Low energy Device will be
     * notified in the below callback.
     */
    private val bluetoothGattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            val deviceAddress = gatt.device.address

            try {
                when (newState) {
                    BluetoothProfile.STATE_CONNECTED -> gatt.discoverServices() // start service discovery
                    BluetoothProfile.STATE_DISCONNECTED -> {
                        try {
                            bluetoothGattMap.remove(deviceAddress)
                            gatt.disconnect()
                            gatt.close()
                        } catch (e: Exception) {
                            info("onConnectionStateChange: ${e.message}")
                        }

                        stopForeground(true)
                        broadcastUpdate(ACTION_GATT_DISCONNECTED, deviceAddress, status)
                    }
                }
            } catch (exception: NullPointerException) {
                exception.printStackTrace()
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            val device = gatt.device
            val deviceAddress = device.address

            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_GATT_CONNECTED, deviceAddress, status)

                // Perform application verification as soon as the service is discovered.
                try {
                    val characteristic = getGattChar(gatt, SERVICE_VSN_SIMPLE_SERVICE, CHAR_APP_VERIFICATION)
                    appVerification(gatt, characteristic, NEW_APP_VERIFICATION_VALUE)
                } catch (e: Exception) {
                    error("onServicesDiscovered: ${e.message}", e)
                }

                for (service in gatt.services) {

                    if (service == null || service.uuid == null) {
                        continue
                    }

                    if (SERVICE_VSN_SIMPLE_SERVICE.equals(service.uuid)) {
                        charVerification = service
                                .getCharacteristic(CHAR_APP_VERIFICATION)

                        // Enable fall detection
                        enableForDetect(gatt,
                                service.getCharacteristic(CHAR_DETECTION_CONFIG),
                                ENABLE_FALL_AND_LONG_PRESS_DETECTION_VALUE)

                        // Set notification for emergency key press and fall detection
                        setCharacteristicNotification(gatt,
                                service.getCharacteristic(CHAR_DETECTION_NOTIFY), true)
                    }

                }
            } else {
                // Service discovery failed close and disconnect the GATT object of the device.
                gatt.disconnect()
                gatt.close()
            }
        }

        /**
         * CallBack when the response available for registered
         * the notification (Battery Status, Fall Detect, Key Press)
         */
        override fun onCharacteristicChanged(gatt: BluetoothGatt,
                                             characteristic: BluetoothGattCharacteristic) {
            broadcastUpdate(ACTION_DATA_RESPONSE, characteristic.uuid.toString(), "",
                    characteristic.value)
        }

        // Callback when the response available for Read Characteristic Request
        override fun onCharacteristicRead(gatt: BluetoothGatt,
                                          characteristic: BluetoothGattCharacteristic, status: Int) {

        }

        // Callback when the response available for Write Characteristic Request
        override fun onCharacteristicWrite(gatt: BluetoothGatt,
                                           characteristic: BluetoothGattCharacteristic, status: Int) {
            broadcastUpdate(ACTION_DATA_RESPONSE, characteristic.uuid.toString(), status)
        }

        // Callback when the response available for Read Descriptor Request
        override fun onDescriptorRead(gatt: BluetoothGatt, descriptor: BluetoothGattDescriptor,
                                      status: Int) {
        }

        // Callback when the response available for Write Descriptor Request
        override fun onDescriptorWrite(gatt: BluetoothGatt, descriptor: BluetoothGattDescriptor,
                                       status: Int) {
            broadcastUpdate(ACTION_DATA_RESPONSE, "enabled key press event", status)
        }

        override fun onReadRemoteRssi(gatt: BluetoothGatt, rssi: Int, status: Int) {
        }
    }

    private var charVerification: BluetoothGattCharacteristic? = null
    private var bluetoothAdapter: BluetoothAdapter? = null
    private var gattService: BluetoothGattService? = null

    companion object {
        const val TAG = "BluetoothLeService"

        const val ACTION_GATT_CONNECTED = "com.vsnmobil.vsnconnect.ACTION_GATT_CONNECTED"
        const val ACTION_GATT_DISCONNECTED = "com.vsnmobil.vsnconnect.ACTION_GATT_DISCONNECTED"
        const val ACTION_DATA_RESPONSE = "com.vsnmobil.vsnconnect.ACTION_DATA_RESPONSE"

        const val EXTRA_DATA = "com.vsnmobil.vsnconnect.EXTRA_DATA"
        const val EXTRA_STATUS = "com.vsnmobil.vsnconnect.EXTRA_STATUS"
        const val EXTRA_ADDRESS = "com.vsnmobil.vsnconnect.EXTRA_ADDRESS"
        const val EXTRA_ACTION_VALUE = "com.vsnmobil.vsnconnect.EXTRA_ACTION_VALUE"
    }

    override fun onCreate() {
        super.onCreate()

        // If bluetooth adapter is not initialized stop the service.
        if (!isBluetoothEnabled()) {
            stopForeground(false)
            stopSelf()
        }

        //To execute the read and write operation in a queue.
        if (!processQueueExecutor.isAlive) {
            processQueueExecutor.start()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        //if blue tooth adapter is not initialized stop the service.
        if (!isBluetoothEnabled()) {
            stopForeground(false)
            stopSelf()
        }

        return Service.START_STICKY
    }

    /**
     * Display [btConnectedNotification] which informs the user of being connected to a
     * bluetooth device
     *
     * @param device the device connected to
     * @param actionIntent intent launched when the notification is clicked
     */
    fun displayNotification(device: BluetoothDevice, actionIntent: PendingIntent) {
        val notification = btConnectedNotification.buildNotification(device, actionIntent)
        startForeground(BtConnectedNotification.NOTIFICATION_ID, notification)
    }

    /**
     * Dismiss the [btConnectedNotification]
     */
    fun dismissNotification() {
        btConnectedNotification.dismissNotification()
    }

    override fun onDestroy() {

        //To stop the foreground service.
        stopForeground(false)

        //Stop the read / write operation queue.
        processQueueExecutor.interrupt()
    }

    /**
     * Manage the BLE service
     *
     * Local binder to bind the service and communicate with this BluetoothLeService class.
     */
    inner class LocalBinder : Binder() {
        val service: BluetoothLeService get() = this@BluetoothLeService
    }

    override fun onBind(arg0: Intent) = binder

    /**
     * Initializes a reference to the local Bluetooth adapter.
     *
     * @return Return true if the initialization is successful.
     */
    fun initialize(): Boolean {
        bluetoothAdapter = bluetoothManager.adapter
        return bluetoothAdapter != null
    }

    /**
     * To write the value to BLE Device
     *
     * @param bluetoothGatt  object of the device.
     * @param characteristic of the device.
     * @param bytes          value to write on to the BLE device.
     */
    private fun writeCharacteristic(bluetoothGatt: BluetoothGatt,
                                    characteristic: BluetoothGattCharacteristic?,
                                    bytes: ByteArray) {
        if (!isStateConnected(bluetoothGatt)) {
            return
        }

        characteristic?.value = bytes

        val requestType = ProcessQueueExecutor.REQUEST_TYPE_WRITE_CHAR
        val readWriteCharacteristic = ReadWriteCharacteristic(requestType, bluetoothGatt, characteristic)

        ProcessQueueExecutor.addProcess(readWriteCharacteristic)
    }

    /**
     * Enables or disables notification on a give characteristic.
     *
     * @param bluetoothGatt  object of the device.
     * @param characteristic Characteristic to act on.
     * @param enabled        If true, enable notification. False otherwise.
     */
    fun setCharacteristicNotification(bluetoothGatt: BluetoothGatt,
                                      characteristic: BluetoothGattCharacteristic,
                                      enabled: Boolean) {

        if (!isStateConnected(bluetoothGatt)) {
            return
        }

        if (!bluetoothGatt.setCharacteristicNotification(characteristic, enabled)) {
            return
        }

        val clientConfig = characteristic.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG) ?: return

        clientConfig.value = if (enabled) {
            ENABLE_NOTIFICATION_VALUE
        } else {
            DISABLE_NOTIFICATION_VALUE
        }

        val requestType = ProcessQueueExecutor.REQUEST_TYPE_WRITE_DESCRIPTOR
        val readWriteCharacteristic = ReadWriteCharacteristic(requestType, bluetoothGatt, clientConfig)
        ProcessQueueExecutor.addProcess(readWriteCharacteristic)
    }

    /**
     * Connects to the GATT server hosted on the Bluetooth LE device.
     *
     * @param address The device address of the destination device.
     * @return Return true if the connection is initiated successfully. The connection result is
     * reported asynchronously through the `BluetoothGattCallback#
     * onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)` callback.
     */
    fun connect(address: String): Boolean {
        val bluetoothGatt = bluetoothGattMap[address]

        bluetoothGatt?.let {
            bluetoothGatt.disconnect()
            bluetoothGatt.close()
        }

        val device = bluetoothAdapter?.getRemoteDevice(address)
        val connectionState = bluetoothManager.getConnectionState(device, BluetoothProfile.GATT)

        if (connectionState == BluetoothProfile.STATE_DISCONNECTED) {
            if (device == null) {
                return false
            }

            // We want to directly connect to the device, so we are setting the
            // autoConnect parameter to false.
            val gatt = device.connectGatt(this, false, bluetoothGattCallback)

            // Add the each BluetoothGatt in to an array list.
            if (!bluetoothGattMap.containsKey(address)) {
                bluetoothGattMap.put(address, gatt)
            } else {
                bluetoothGattMap.remove(address)
                bluetoothGattMap.put(address, gatt)
            }
        } else {
            return false
        }
        return true
    }

    /**
     * To disconnect the connected Blue tooth Low energy Device from the APP.
     *
     * @param bluetoothGatt pass the GATT object of the device which need to be disconnect.
     */
    fun disconnect(bluetoothGatt: BluetoothGatt?) {
        bluetoothGatt?.let {
            val device = bluetoothGatt.device
            val deviceAddress = device.address

            try {
                bluetoothGattMap.remove(deviceAddress)

                stopForeground(true)

                bluetoothGatt.disconnect()
                bluetoothGatt.close()
            } catch (e: Exception) {
                error("disconnect: ${e.message}")
            }
        }
    }

    /**
     * To check the connection status of the GATT object.
     *
     * @param bluetoothGatt pass the GATT object of the device.
     * @return If connected it will return true, false otherwise.
     */
    private fun isStateConnected(bluetoothGatt: BluetoothGatt): Boolean {
        if (bluetoothAdapter == null) {
            return false
        }

        val device = bluetoothGatt.device
        val deviceAddress = device.address
        val bluetoothDevice = bluetoothAdapter?.getRemoteDevice(deviceAddress)
        val connectionState = bluetoothManager.getConnectionState(bluetoothDevice, BluetoothProfile.GATT)

        return connectionState == BluetoothProfile.STATE_CONNECTED
    }

    /**
     * To write the value to BLE Device for APP verification
     *
     * @param bluetoothGatt               object of the device.
     * @param bluetoothGattCharacteristic of the device.
     */
    fun appVerification(bluetoothGatt: BluetoothGatt,
                        bluetoothGattCharacteristic: BluetoothGattCharacteristic?,
                        value: ByteArray) {
        writeCharacteristic(bluetoothGatt, bluetoothGattCharacteristic, value)
    }

    /**
     * To write the value to BLE Device for Emergency / Fall alert
     *
     * @param bluetoothGatt               object of the device.
     * @param bluetoothGattCharacteristic characteristic of the device.
     * @param bytes                       value to write on to the BLE device.
     */
    fun enableForDetect(bluetoothGatt: BluetoothGatt,
                        bluetoothGattCharacteristic: BluetoothGattCharacteristic,
                        bytes: ByteArray) {
        writeCharacteristic(bluetoothGatt, bluetoothGattCharacteristic, bytes)
    }

    /**
     * To get the characteristic of the corresponding BluetoothGatt object and service UUID and
     * Characteristic UUID.
     *
     * @param bluetoothGatt      object of the device.
     * @param serviceUUID        UUID.
     * @param characteristicUUID UUID.
     * @return BluetoothGattCharacteristic of the given service and Characteristic UUID.
     */
    fun getGattChar(bluetoothGatt: BluetoothGatt, serviceUUID: UUID,
                    characteristicUUID: UUID): BluetoothGattCharacteristic? {
        gattService = bluetoothGatt.getService(serviceUUID)

        return gattService?.getCharacteristic(characteristicUUID)
    }

    /**
     * Broadcast the values to the UI if the application is in foreground.
     *
     * @param action      intent action.
     * @param value       value to update to the receiver.
     * @param actionValue
     */
    private fun broadcastUpdate(action: String, value: String, address: String,
                                actionValue: ByteArray) {
        Log.d(TAG, "broadcastUpdate() called with: action = [$action], value = [$value], " +
                "address = [$address]")

        val intent = Intent(action)
        intent.action = action
        intent.putExtra(EXTRA_DATA, value)
        intent.putExtra(EXTRA_ADDRESS, address)
        intent.putExtra(EXTRA_ACTION_VALUE, actionValue)
        sendBroadcast(intent)
    }

    /**
     * Broadcast the values to the UI if the application is in foreground.
     *
     * @param action  intent action.
     * @param address address of the device.
     * @param status  connection status of the device.
     */
    fun broadcastUpdate(action: String, address: String, status: Int) {
        debug("broadcastUpdate() called with: action = [$action], address = [$address], status = [$status]")

        val intent = Intent(action)
        intent.putExtra(EXTRA_DATA, address)
        intent.putExtra(EXTRA_STATUS, status)
        sendBroadcast(intent)
    }

    /**
     * To check the device bluetooth is enabled or not.
     *
     * @return boolean Bluetooth is enabled / disabled.
     */
    private fun isBluetoothEnabled() = bluetoothManager.adapter.isEnabled
}