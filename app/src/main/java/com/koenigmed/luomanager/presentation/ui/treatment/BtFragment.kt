package com.koenigmed.luomanager.presentation.ui.treatment

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import com.koenigmed.luomanager.R
import com.koenigmed.luomanager.presentation.ui.global.BaseFragment

abstract class BtFragment: BaseFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val btAdapter = BluetoothAdapter.getDefaultAdapter()
        if (btAdapter == null) {
            showMessage(getString(R.string.error_device_does_not_support_bluetooth))
            return
        }
        activity!!.registerReceiver(btReceiver, IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED))
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    private val btReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (action == BluetoothAdapter.ACTION_STATE_CHANGED) {
                val state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR)
                when (state) {
                    BluetoothAdapter.STATE_OFF -> {
                        onBluetoothStateChanged(false)
                    }
                    BluetoothAdapter.STATE_TURNING_OFF -> {
                    }
                    BluetoothAdapter.STATE_ON -> {
                        onBluetoothStateChanged(true)
                    }
                    BluetoothAdapter.STATE_TURNING_ON -> {
                    }
                }
            }
        }
    }

    abstract fun onBluetoothStateChanged(enabled: Boolean)

    override fun onDestroy() {
        super.onDestroy()
        try {
            activity?.unregisterReceiver(btReceiver)
        } catch (ignored: Exception) {
        }
    }
}