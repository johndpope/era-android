package com.josiassena.bluetooth

import android.app.Activity
import android.app.PendingIntent
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.*
import android.os.IBinder
import android.util.Log
import com.josiassena.bluetooth.data.ActionValue
import com.josiassena.bluetooth.data.BluetoothDeviceRSSI
import com.josiassena.bluetooth.helpers.ActionValueMessage
import com.josiassena.bluetooth.listeners.BtConnectionListener
import com.josiassena.bluetooth.service.BluetoothLeService
import io.reactivex.subjects.PublishSubject
import org.jetbrains.anko.AnkoLogger

/**
 * Scans for normal Bluetooth devices and Bluetooth low energy devices
 *
 * @author Josias Sena
 */
class BluetoothHandler(private val context: Context) : AnkoLogger {

    val actionListener: PublishSubject<ActionValue> = PublishSubject.create()

    private val leDevicesListener: PublishSubject<BluetoothDeviceRSSI> = PublishSubject.create()
    private val normalDeviceListener: PublishSubject<BluetoothDeviceRSSI> = PublishSubject.create()
    private val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    private val bluetoothAdapter = bluetoothManager.adapter
    private val bluetoothLeScanner = bluetoothAdapter?.bluetoothLeScanner

    private val normalBluetoothReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action

            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND == action) {

                // Get the BluetoothDevice object from the Intent
                val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)

                // Get the rSSI from the Intent
                val rSSI = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, 0)

                leDevicesListener.onNext(BluetoothDeviceRSSI(device, rSSI.toInt()))
            }
        }
    }

    private val bluetoothLeScanCallback = object : ScanCallback() {

        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            leDevicesListener.onNext(BluetoothDeviceRSSI(result.device, result.rssi))
        }

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)

            when (errorCode) {
                SCAN_FAILED_ALREADY_STARTED -> {
                    val throwable = Throwable("Fails to start scan as BLE scan with the same settings is already started by the app.")
                    leDevicesListener.onError(throwable)
                }
                SCAN_FAILED_APPLICATION_REGISTRATION_FAILED -> {
                    val throwable = Throwable("Fails to start scan as app cannot be registered.")
                    leDevicesListener.onError(throwable)
                }
                SCAN_FAILED_INTERNAL_ERROR -> {
                    val throwable = Throwable("Fails to start scan due an internal error")
                    leDevicesListener.onError(throwable)
                }
                SCAN_FAILED_FEATURE_UNSUPPORTED -> {
                    val throwable = Throwable("Fails to start power optimized scan as this feature is not supported.")
                    leDevicesListener.onError(throwable)
                }
            }
        }
    }

    private val serviceConnection = object : ServiceConnection {

        override fun onServiceConnected(componentName: ComponentName, service: IBinder) {
            bluetoothLeService = (service as BluetoothLeService.LocalBinder).service

            if (!(bluetoothLeService?.initialize() as Boolean)) {
                val throwable = Throwable("Unable to initialize Bluetooth")
                btConnectionListener?.onError(throwable)
            }

            val macAddress = connectedBluetoothDevice?.address.toString()

            // Automatically connects to the device upon successful start-up initialization.
            isConnected = bluetoothLeService?.connect(macAddress) as Boolean

            if (isConnected) {
                registerGattUpdates()
            }
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            bluetoothLeService = null
        }
    }

    /**
     * Handles various events fired by the Service.
     *
     * ACTION_GATT_CONNECTED: connected to a GATT server.
     * ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
     * ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
     * ACTION_DATA_AVAILABLE: received data from the device. This can be a result of read
     * or notification operations.
     */
    private val gattUpdateBroadcastReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action

            when (action) {
                BluetoothLeService.ACTION_GATT_CONNECTED -> {
                    btConnectionListener?.onConnected(connectedBluetoothDevice)
                    isConnected = true
                }
                BluetoothLeService.ACTION_GATT_DISCONNECTED -> {
                    isConnected = false
                    btConnectionListener?.onDisconnected()
                    context.unbindService(serviceConnection)
                }
                BluetoothLeService.ACTION_DATA_RESPONSE -> {
                    val data = intent.getStringExtra(BluetoothLeService.EXTRA_DATA)
                    val actionValues = intent.getByteArrayExtra(BluetoothLeService.EXTRA_ACTION_VALUE)
                    Log.d(TAG, "gattUpdateBroadcastReceiver() data = $data , " +
                            "info = [${ActionValueMessage.getMessageFromValue(actionValues)}]")

                    data?.let {
                        actionValues?.let {
                            when (actionValues[0].toInt()) {
                                ActionValue.PRESS.value -> {
                                    actionListener.onNext(ActionValue.PRESS)
                                }
                                ActionValue.RELEASE.value -> {
                                    actionListener.onNext(ActionValue.RELEASE)
                                }
                                ActionValue.LONG_PRESS.value -> {
                                    actionListener.onNext(ActionValue.LONG_PRESS)
                                }
                                ActionValue.FALL.value -> {
                                    actionListener.onNext(ActionValue.FALL)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private var btConnectionListener: BtConnectionListener? = null
    private var connectedBluetoothDevice: BluetoothDevice? = null
    private var bluetoothLeService: BluetoothLeService? = null
    private var isConnected: Boolean = false

    companion object {
        private const val TAG = "BluetoothHandler"
    }

    /**
     * Return true if Bluetooth is available.
     *
     * @return true if [bluetoothAdapter] is not null, otherwise Bluetooth is
     * not supported on this hardware platform
     */
    fun isBluetoothAvailable(): Boolean = bluetoothAdapter != null

    /**
     * Return true if Bluetooth is currently enabled and ready for use.
     *
     * Equivalent to:
     * `getBluetoothState() == STATE_ON`
     *
     * Requires [android.Manifest.permission.BLUETOOTH]
     *
     * @return true if the local adapter is turned on
     */
    fun isBluetoothEnabled() = bluetoothAdapter.isEnabled

    /**
     * This will issue a request to enable Bluetooth through the system settings (without stopping
     * your application) via ACTION_REQUEST_ENABLE action Intent.
     *
     * @param activity Activity
     * @param requestCode request code
     */
    fun enableBluetooth(activity: Activity?, requestCode: Int) {
        if (!bluetoothAdapter.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            activity?.startActivityForResult(enableBtIntent, requestCode)
        }
    }

    /**
     * Start scanning for normal Bluetooth devices
     *
     * @return a publish subject for reactive listening.
     * @see normalDeviceListener
     */
    fun scanForNormalBluetoothDevices(): PublishSubject<BluetoothDeviceRSSI> {
        bluetoothAdapter?.startDiscovery()
        return normalDeviceListener
    }

    /**
     * Stop scanning for normal Bluetooth devices
     */
    fun stopScanningForNormalBluetoothDevices() {
        bluetoothAdapter?.cancelDiscovery()
    }

    /**
     * Start scanning for Bluetooth low energy devices
     *
     * @return a publish subject for reactive listening.
     * @see leDevicesListener
     */
    fun scanForLeDevices(): PublishSubject<BluetoothDeviceRSSI> {
        bluetoothLeScanner?.startScan(bluetoothLeScanCallback)
        return leDevicesListener
    }

    /**
     * Stop scanning for Bluetooth low energy devices
     */
    fun stopScanningForBluetoothLeDevices() {
        bluetoothLeScanner?.stopScan(bluetoothLeScanCallback)
    }

    /**
     * Only required when scanning for normal devices.
     *
     * Registers the [normalBluetoothReceiver]
     */
    fun register() {
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        context.registerReceiver(normalBluetoothReceiver, filter)
    }

    /**
     * @return true if connected to a bluetooth device, false otherwise
     */
    fun isConnectedToBluetooth() = isConnected

    /**
     * @return the current device we are currently connected to
     */
    fun getConnectedDevice() = connectedBluetoothDevice

    /**
     * Connect to a [bluetoothDevice]
     *
     * @param bluetoothDevice the device to connect to
     */
    fun connectTo(bluetoothDevice: BluetoothDevice) {
        connectedBluetoothDevice = bluetoothDevice

        val gattServiceIntent = Intent(context, BluetoothLeService::class.java)
        context.bindService(gattServiceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
        context.startService(gattServiceIntent)
    }

    fun displayConnectionNotification(actionIntent: PendingIntent) {
        connectedBluetoothDevice?.let {
            bluetoothLeService?.displayNotification(it, actionIntent)
        }
    }

    fun dismissConnectionNotification() {
        bluetoothLeService?.dismissNotification()
    }

    private fun registerGattUpdates() {
        val intentFilter = IntentFilter().apply {
            addAction(BluetoothLeService.ACTION_GATT_CONNECTED)
            addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED)
            addAction(BluetoothLeService.ACTION_DATA_RESPONSE)
        }

        context.registerReceiver(gattUpdateBroadcastReceiver, intentFilter)
    }

    /**
     * Disconnect from a [BluetoothDevice]
     *
     * @param bluetoothDevice the bluetooth device to disconnect from
     */
    fun disconnect(bluetoothDevice: BluetoothDevice?) {
        val address = bluetoothDevice?.address
        val bluetoothGatt = bluetoothLeService?.bluetoothGattMap?.get(address)
        bluetoothLeService?.disconnect(bluetoothGatt)
    }

    fun setConnectionListener(btConnectionListener: BtConnectionListener? = null) {
        this.btConnectionListener = btConnectionListener
    }

}
