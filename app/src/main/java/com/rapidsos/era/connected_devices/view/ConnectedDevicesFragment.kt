package com.rapidsos.era.connected_devices.view

import android.app.AlertDialog
import android.app.PendingIntent
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hannesdorfmann.mosby3.mvp.MvpFragment
import com.josiassena.bluetooth.BluetoothHandler
import com.rapidsos.era.R
import com.rapidsos.era.R.id.rvConnectedDevices
import com.rapidsos.era.application.App
import com.rapidsos.era.connected_devices.presenter.ConnectedDevicesPresenterImpl
import com.rapidsos.era.connected_devices.rec_view.ConnectedDevicesAdapter
import com.rapidsos.era.main.view.MainActivity
import com.rapidsos.utils.extensions.hide
import com.rapidsos.utils.extensions.show
import com.rapidsos.utils.extensions.snack
import kotlinx.android.synthetic.main.fragment_connecteddevices.*
import org.jetbrains.anko.AnkoLogger
import java.util.*
import javax.inject.Inject

class ConnectedDevicesFragment : MvpFragment<ConnectedDevicesView, ConnectedDevicesPresenterImpl>(),
        ConnectedDevicesView, AnkoLogger {

    private val devicesAdapter = ConnectedDevicesAdapter()
    private val btDisconnectDialog: AlertDialog by lazy {
        AlertDialog.Builder(context)
                .setCancelable(false)
                .setTitle(getString(R.string.confirmation))
                .setMessage(getString(R.string.confirm_bt_disconnect))
                .setPositiveButton(R.string.yes, { _, _ ->
                    presenter.disconnect(connectedBluetoothDevice)
                })
                .setNegativeButton(R.string.no, { _, _ ->
                }).create()
    }

    @Inject
    lateinit var bluetoothHandler: BluetoothHandler

    @Inject
    lateinit var presenterImpl: ConnectedDevicesPresenterImpl

    private var connectedBluetoothDevice: BluetoothDevice? = null

    companion object {
        const val ACTION_CONNECTED_TO_BT = "connected_to_bt_device"
    }

    override fun createPresenter() = presenterImpl

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_connecteddevices, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        App.component.inject(this)
        super.onViewCreated(view, savedInstanceState)

        if (bluetoothHandler.isConnectedToBluetooth()) {
            connectedBluetoothDevice = bluetoothHandler.getConnectedDevice()
            displayConnectedView()
        } else {
            scanForBluetoothDevices()
        }

        initRecView()

        loadingView.setOnRefreshListener { scanForBluetoothDevices() }
    }

    /**
     * Scan for nearby bluetooth devices
     */
    private fun scanForBluetoothDevices() {
        devicesAdapter.clearItems()
        presenter.scanForBluetoothDevices()
    }

    override fun onPause() {
        super.onPause()
        presenter.stopScanningForBluetoothDevices()
    }

    /**
     * Initialize the connected devices RecyclerView ([rvConnectedDevices])
     */
    private fun initRecView() {
        rvConnectedDevices.adapter = devicesAdapter
        rvConnectedDevices.setItemViewCacheSize(30)

        devicesAdapter.getItemClickListener().subscribe { bluetoothDevice ->
            showConnectionDialog(bluetoothDevice)
        }
    }

    /**
     * Display a confirmation dialog for connecting to a [bluetoothDevice]
     *
     * @param bluetoothDevice the device to connect to
     */
    private fun showConnectionDialog(bluetoothDevice: BluetoothDevice) {
        AlertDialog.Builder(context)
                .setCancelable(false)
                .setTitle(getString(R.string.bt_connection))
                .setMessage("Would you like to connect to ${bluetoothDevice.address}?")
                .setPositiveButton(R.string.yes, { _, _ ->
                    val pendingIntent = PendingIntent.getActivity(context, 0,
                            Intent(context, MainActivity::class.java).apply {
                                action = ACTION_CONNECTED_TO_BT
                            }, 0)

                    presenter.connect(bluetoothDevice, pendingIntent)
                })
                .setNegativeButton(getString(R.string.no), { _, _ ->
                }).show()
    }

    private fun displayConnectedView() {
        loadingView.hide()
        connectedView.show()

        val deviceName = connectedBluetoothDevice?.name
        val format = getString(R.string.connected_to)
        tvConnectedTitle.text = String.format(Locale.getDefault(), format, deviceName)

        tvMacAddress.text = connectedBluetoothDevice?.address

        btnDisconnect.setOnClickListener {
            btDisconnectDialog.show()
        }
    }

    private fun displayNormalViews() {
        connectedView.hide()
        noBtDevicesView.hide()

        loadingView.show()
        rvConnectedDevices.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.unSubscribe()
    }

    override fun addBluetoothDeviceToList(bluetoothDevice: BluetoothDevice) {
        noBtDevicesView.hide()
        rvConnectedDevices.show()

        devicesAdapter.addDevice(bluetoothDevice)
    }

    override fun displayBtUnavailableError() =
            rvConnectedDevices.snack(getString(R.string.bt_unavailable))

    override fun enableBluetooth() =
            bluetoothHandler.enableBluetooth(activity, 138)

    override fun showLoading() {
        loadingView.isRefreshing = true
    }

    override fun hideLoading() {
        loadingView.isRefreshing = false
    }

    override fun onConnectedToDevice(bluetoothDevice: BluetoothDevice?) {
        this.connectedBluetoothDevice = bluetoothDevice
        displayConnectedView()
    }

    override fun onDisconnectedFromBluetooth() {
        displayNormalViews()
        scanForBluetoothDevices()
    }

    override fun showError(error: String) = rvConnectedDevices.snack(error)
}
