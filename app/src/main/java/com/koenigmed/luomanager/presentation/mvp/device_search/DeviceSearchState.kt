package com.koenigmed.luomanager.presentation.mvp.device_search

import android.bluetooth.BluetoothDevice

sealed class DeviceSearchState {
        class NoBluetooth: DeviceSearchState()
        class Running: DeviceSearchState()
        class FewDevices(val devices: List<BluetoothDevice>): DeviceSearchState()
        data class Connected(val deviceName: String): DeviceSearchState()
}
