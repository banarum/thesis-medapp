package com.koenigmed.luomanager.data.decice

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGatt.GATT_SUCCESS
import android.bluetooth.BluetoothGattCharacteristic
import com.google.gson.GsonBuilder
import com.koenigmed.luomanager.data.model.device.*
import com.koenigmed.luomanager.data.server.deserializer.BooleanTypeAdapter
import com.koenigmed.luomanager.data.server.deserializer.DateTimeTypeAdapter
import com.koenigmed.luomanager.data.server.deserializer.TimeTypeAdapter
import com.koenigmed.luomanager.extension.isValidJson
import org.threeten.bp.LocalDateTime
import org.threeten.bp.LocalTime
import timber.log.Timber

class DeviceApi(private val gatt: BluetoothGatt,
                private val characteristic: BluetoothGattCharacteristic) {

    private var deviceStatus = DeviceStatus.READY

    private val gson by lazy {
        GsonBuilder()
                .registerTypeAdapter(Boolean::class.javaObjectType, BooleanTypeAdapter())
                .registerTypeAdapter(LocalTime::class.java, TimeTypeAdapter())
                .registerTypeAdapter(LocalDateTime::class.java, DateTimeTypeAdapter())
                .create()
    }
    private var commandChunks: List<String>? = null
    private var chunkIndexToWrite = 0
    private lateinit var progressCallback: (Progress) -> Unit

    private var responseString = ""
    private var isError = false

    fun syncTime(progressCallback: (Progress) -> Unit) {
        executeCommand(progressCallback, SyncTimeCommand(LocalDateTime.now()))
    }

    fun sendProgram(programCommand: ProgramCommand, progressCallback: (Progress) -> Unit) {
        programCommand.tasks?.forEach { it.amperage = it.amperage.coerceAtMost(10) }//todo! remove crutch
        executeCommand(progressCallback, programCommand)
    }

    fun sendStart(progressCallback: (Progress) -> Unit) {
        executeCommand(progressCallback, StartCommand())
    }

    fun sendStop(progressCallback: (Progress) -> Unit) {
        executeCommand(progressCallback, StopCommand())
    }

    private fun executeCommand(progressCallback: (Progress) -> Unit, command: DeviceCommand) {
        if (deviceStatus == DeviceStatus.COMMAND_SENDING) {
            return
        }
        deviceStatus = DeviceStatus.COMMAND_SENDING
        this.progressCallback = progressCallback
        sendCommand(command)
    }

    fun onCharacteristicWrite(status: Int, response: String?) {
        if (isError) return
        if (status != GATT_SUCCESS) {
            progressCallback.invoke(Progress(-1, response))
            onCommandFinished()
            return
        }
        val percent = chunkIndexToWrite * 100 / commandChunks!!.size
        if (percent < 100) progressCallback.invoke(Progress(percent))
        if (chunkIndexToWrite < commandChunks!!.size) {
            writeChunk(commandChunks!![chunkIndexToWrite++])
        } else {
            onCommandFinished()
        }
    }

    private fun sendCommand(command: Any) {
        val json = gson.toJson(command)
        Timber.d("sendCommand $json")
        isError = false
        commandChunks = json.chunked(CHUNK_LETTERS_COUNT)
        writeChunk(commandChunks!![chunkIndexToWrite++])
    }

    private fun writeChunk(chunk: String) {
        Timber.d("writeChunk $chunk")
        val b = chunk.toByteArray()
        println("b " + b.size)
        characteristic.setValue(chunk)
        gatt.writeCharacteristic(characteristic)
    }

    private fun onCommandFinished() {
        deviceStatus = DeviceStatus.READY
        commandChunks = null
        chunkIndexToWrite = 0
    }

    fun onCharacteristicChanged(chunk: String) {
        responseString += chunk
        if (responseString.isValidJson()) {
            val response = gson.fromJson(responseString, JsonDeviceResponse::class.java)
            if (response.isOk()) {
                progressCallback.invoke(Progress(100))
            } else {
                isError = true
                progressCallback.invoke(Progress(-1, responseString))
            }
            responseString = ""
        }
    }

    fun onDeviceDisconnected() {
        onCommandFinished()
    }

    companion object {
        const val CHUNK_LETTERS_COUNT = 20
    }

}