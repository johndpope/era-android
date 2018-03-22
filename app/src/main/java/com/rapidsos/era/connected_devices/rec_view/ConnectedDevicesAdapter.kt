package com.rapidsos.era.connected_devices.rec_view

import android.bluetooth.BluetoothDevice
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.rapidsos.era.R
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.item_connecteddevices.view.*
import org.jetbrains.anko.AnkoLogger

class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)

class ConnectedDevicesAdapter :
        RecyclerView.Adapter<ViewHolder>(), AnkoLogger {

    private val listener: PublishSubject<BluetoothDevice> = PublishSubject.create()
    private val devices = arrayListOf<BluetoothDevice>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_connecteddevices, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val view = holder.view
        val device = devices[position]

        view.tvDeviceName.text = if (device.name.isNullOrEmpty()) {
            "Unknown"
        } else {
            device.name
        }

        view.tvDeviceAddress.text = device.address

        view.setOnClickListener {
            listener.onNext(device)
        }
    }

    override fun getItemCount() = devices.size

    fun addDevice(bluetoothDevice: BluetoothDevice) {
        if (!devices.contains(bluetoothDevice)) {
            devices.add(itemCount, bluetoothDevice)
            notifyItemInserted(itemCount)
        }
    }

    fun clearItems() {
        devices.clear()
        notifyDataSetChanged()
    }

    fun getItemClickListener() = listener
}
