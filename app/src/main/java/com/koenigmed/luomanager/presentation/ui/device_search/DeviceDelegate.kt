package com.koenigmed.luomanager.presentation.ui.device_search

import android.bluetooth.BluetoothDevice
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.hannesdorfmann.adapterdelegates3.AdapterDelegate
import com.koenigmed.luomanager.R
import com.koenigmed.luomanager.extension.inflate
import kotlinx.android.synthetic.main.item_device.view.*

class DeviceDelegate(
        private val clickListener: (BluetoothDevice) -> Unit
) : AdapterDelegate<MutableList<Any>>() {

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder =
            DeviceViewHolder(parent.inflate(R.layout.item_device))

    override fun isForViewType(items: MutableList<Any>, position: Int): Boolean =
            items[position] is BluetoothDevice

    override fun onBindViewHolder(items: MutableList<Any>, position: Int, holder: RecyclerView.ViewHolder, payloads: MutableList<Any>) {
        (holder as DeviceViewHolder).bind(items[position] as BluetoothDevice)
    }

    private inner class DeviceViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind(device: BluetoothDevice) {
            with(itemView) {
                deviceName.text = device.name
                setOnClickListener {
                    clickListener.invoke(device)
                }
            }
        }
    }
}