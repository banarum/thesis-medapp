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
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.observables.ConnectableObservable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import org.threeten.bp.LocalDateTime
import org.threeten.bp.LocalTime
import timber.log.Timber
import java.util.*
import kotlin.collections.HashMap

class DeviceBtApi(private val gatt: BluetoothGatt,
                private val characteristic: BluetoothGattCharacteristic) : BtApi<DeviceCommand, JsonDeviceResponse> {

    private var deviceStatus = DeviceStatus.READY

    private val resultSubjects: Queue<PublishSubject<ProgressResponse<JsonDeviceResponse>>> = LinkedList<PublishSubject<ProgressResponse<JsonDeviceResponse>>>()

    private val gson by lazy {
        GsonBuilder()
                .registerTypeAdapter(Boolean::class.javaObjectType, BooleanTypeAdapter())
                .registerTypeAdapter(LocalTime::class.java, TimeTypeAdapter())
                .registerTypeAdapter(LocalDateTime::class.java, DateTimeTypeAdapter())
                .create()
    }
    private var commandChunks: List<String>? = null
    private var chunkIndexToWrite = 0

    private var currentPublisher: PublishSubject<ProgressResponse<JsonDeviceResponse>>? = null

    private var responseString = ""
    private var isError = false

    private val chainedMap: HashMap<PublishSubject<ProgressResponse<JsonDeviceResponse>>, Boolean> = HashMap()


    override fun executeCommand(command: DeviceCommand, chained: Boolean): Observable<ProgressResponse<JsonDeviceResponse>> {
        if (deviceStatus == DeviceStatus.COMMAND_SENDING) {
            return PublishSubject.empty()
        }
        deviceStatus = DeviceStatus.COMMAND_SENDING
        sendCommand(command)
        val subject = PublishSubject.create<ProgressResponse<JsonDeviceResponse>>()
        currentPublisher = subject
        chainedMap[subject] = chained
        resultSubjects.add(subject)
        return subject
    }

    override fun onCharacteristicWrite(status: Int, response: String?) {
        if (isError) return
        if (status != GATT_SUCCESS) {
            onCommandFinished()
            return
        }

        val percent: Float = chunkIndexToWrite.toFloat() / commandChunks!!.size
        if (percent < 1.0f) onUploadProgress(percent)
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
        responseString = ""
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

    override fun onCharacteristicChanged(chunk: String) {
        responseString += chunk
        Timber.d(responseString)
        if (responseString.isValidJson()) {

            val response = gson.fromJson(responseString, JsonDeviceResponse::class.java)
            onResponseReceived(response)
            responseString = ""
        }
    }

    private fun onResponseReceived(response: JsonDeviceResponse?) {
        if (response != null && response.isOk()) {
            onResponseSuccess(response)
        }else if (response != null && !response.isOk()) {
            onResponseFail(response)
        }else if (response == null) {
            onResponseMalformed()
        }
    }

    private fun onUploadProgress(progress: Float, resultSubject: PublishSubject<ProgressResponse<JsonDeviceResponse>>? = getNextResultSubject()) =
            resultSubject?.onNext(ProgressResponse(null, progress))

    private fun onResponseSuccess(response: JsonDeviceResponse, resultSubject: PublishSubject<ProgressResponse<JsonDeviceResponse>>? = takeNextResultSubject()) =
            resultSubject?.apply {
                onNext(ProgressResponse(response, 1f))
                if (chainedMap[resultSubject]!=true)
                    onComplete()
                chainedMap.remove(resultSubject)
            }

    private fun onResponseFail(response: JsonDeviceResponse, resultSubject: PublishSubject<ProgressResponse<JsonDeviceResponse>>? = takeNextResultSubject()) =
            resultSubject?.onError(Exception("${response.response} ${response.errorCode}"))

    private fun onResponseMalformed(resultSubject: PublishSubject<ProgressResponse<JsonDeviceResponse>>? = takeNextResultSubject()) =
            resultSubject?.onError(Exception("Malformed response."))

    private fun onResponseLost(resultSubject: PublishSubject<ProgressResponse<JsonDeviceResponse>>? = takeNextResultSubject()) =
            resultSubject?.onError(Exception("Connection error."))

    private fun takeNextResultSubject(): PublishSubject<ProgressResponse<JsonDeviceResponse>>? = resultSubjects.poll()

    private fun getNextResultSubject(): PublishSubject<ProgressResponse<JsonDeviceResponse>>? = resultSubjects.peek()

    override fun onDeviceDisconnected() {
        onCommandFinished()
    }

    companion object {
        const val CHUNK_LETTERS_COUNT = 20
    }
}

