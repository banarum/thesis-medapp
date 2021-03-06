package com.koenigmed.luomanager.data.decice

import android.bluetooth.BluetoothGatt

import android.bluetooth.BluetoothGattCharacteristic

import com.koenigmed.luomanager.data.model.device.*
import io.reactivex.Observable

import org.threeten.bp.LocalDateTime
import java.util.concurrent.TimeUnit

class DeviceApi(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic): BtEventReceiver {

    private val btApi: BtApi<DeviceCommand, JsonDeviceResponse> = DeviceBtApi(gatt, characteristic)

    fun syncTime(): Observable<ProgressResponse<JsonDeviceResponse>> {
        return btApi.executeCommand(SyncTimeCommand(LocalDateTime.now()))
    }

    fun sendProgram(programCommand: ProgramCommand): Observable<ProgressResponse<JsonDeviceResponse>> {
        programCommand.tasks?.forEach { it.amperage = it.amperage.coerceAtMost(10) }//todo! remove crutch
        return btApi.executeCommand(programCommand)
    }

    fun sendStart(): Observable<ProgressResponse<JsonDeviceResponse>> {
        return btApi.executeCommand(StartCommand())
    }

    fun sendStop(): Observable<ProgressResponse<JsonDeviceResponse>> {
        return btApi.executeCommand(StopCommand())
    }

    private fun <T> throwErrorIf(isError: Boolean, message: String, obj: T): T {
        if (isError) throw Exception(message) else return obj
    }

    fun getChargeLevel(): Observable<ProgressResponse<JsonDeviceResponse>> {
        return btApi.executeCommand(DeviceCommand("getVoltage"))
                .map { throwErrorIf(it.isCompleted() && it.result!!.voltage == null, "Undefined command", it) }
    }

    fun getRssi() = btApi.getRssi()

    override fun onCharacteristicChanged(chunk: String) = btApi.onCharacteristicChanged(chunk)
    override fun onCharacteristicWrite(status: Int, response: String?) = btApi.onCharacteristicWrite(status, response)
    override fun onDeviceDisconnected() = btApi.onDeviceDisconnected()
    override fun onReadRemoteRssi(gatt: BluetoothGatt?, rssi: Int, status: Int) = btApi.onReadRemoteRssi(gatt, rssi, status)

    companion object {
        fun mvToPercent(mv: String) = (((mv.dropLast(2).toFloat() - 3200f) / (4200f - 3200f))*100).toInt()
    }
}

