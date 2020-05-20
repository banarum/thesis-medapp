package com.koenigmed.luomanager.data.decice

import android.bluetooth.BluetoothGatt
import io.reactivex.Observable

interface BtEventReceiver {
    fun onCharacteristicWrite(status: Int, response: String?)
    fun onCharacteristicChanged(chunk: String)
    fun onDeviceDisconnected()
    fun onReadRemoteRssi(gatt: BluetoothGatt?, rssi: Int, status: Int)
}

