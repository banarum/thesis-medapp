package com.koenigmed.luomanager.data.decice

import io.reactivex.Observable

interface BtEventReceiver {
    fun onCharacteristicWrite(status: Int, response: String?)
    fun onCharacteristicChanged(chunk: String)
    fun onDeviceDisconnected()
}

