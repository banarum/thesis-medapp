package com.koenigmed.luomanager.domain.interactor.device

import android.annotation.SuppressLint
import android.bluetooth.*
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.bluetooth.le.ScanSettings.CALLBACK_TYPE_ALL_MATCHES
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.ParcelUuid
import com.koenigmed.luomanager.R
import com.koenigmed.luomanager.data.decice.DeviceApi
import com.koenigmed.luomanager.data.decice.Progress
import com.koenigmed.luomanager.data.decice.ProgressResponse
import com.koenigmed.luomanager.data.mapper.program.ProgramMapper
import com.koenigmed.luomanager.data.model.device.JsonDeviceResponse
import com.koenigmed.luomanager.data.repository.prefs.PrefsRepository
import com.koenigmed.luomanager.domain.model.program.MyoProgram
import com.koenigmed.luomanager.extension.toUUID
import com.koenigmed.luomanager.system.IResourceManager
import com.koenigmed.luomanager.util.PermissionUtil
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.disposables.Disposable
import io.reactivex.observables.ConnectableObservable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit

import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.sqrt

class BtInteractor
@Inject constructor(resourceManager: IResourceManager,
                    private val programMapper: ProgramMapper,
                    private val prefsRepository: PrefsRepository) {

    private val context: Context = resourceManager.getContext()
    private lateinit var btStateListener: (Boolean) -> Unit
    private var deviceSearchListener: DeviceSearchListener? = null
    private lateinit var unbindListener: (Boolean) -> Unit

    var btState = BT_CONNECTION_INACTIVE

    private lateinit var stateEmmiter: ObservableEmitter<Int>
    var stateObservable: ConnectableObservable<Int> = Observable.create<Int> { emitter -> stateEmmiter = emitter}.publish()

    private val stateObserverDisposable: Disposable = stateObservable.subscribe {
        this.btState = it
    }

    val chargeNotifier: Observable<ProgressResponse<JsonDeviceResponse>> = Observable.interval(0, 10, TimeUnit.SECONDS)
            .flatMap { if (deviceApi!= null) deviceApi!!.getChargeLevel() else Observable.empty() }
            .retry()
            .publish().autoConnect()

    val rssiNotifier: Observable<BtPower> = Observable.interval(0, 500, TimeUnit.MILLISECONDS)
            .flatMap { if (deviceApi!= null) deviceApi!!.getRssi() else Observable.empty() }
            .map { BtPower.getPower(it) }
            .retry()
            .publish().autoConnect()

    interface DeviceSearchListener {
        fun onError(message: String)
        fun onDevicesFound(devices: List<BluetoothDevice>)
        fun onDevicePaired(deviceName: String)
    }

    private val btAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private var gatt: BluetoothGatt? = null
    var deviceApi: DeviceApi? = null

    private val btReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Timber.d("onReceive intent.action " + intent.action)
            if (intent.action == BluetoothAdapter.ACTION_STATE_CHANGED) {
                onBtStateChanged(intent)
            }
        }
    }

    private val leScanCallback = object : ScanCallback() {
        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            Timber.d("onScanFailed $errorCode")
        }

        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            Timber.d("onScanResult $callbackType, result $result")
            if (callbackType == CALLBACK_TYPE_ALL_MATCHES) {
                btAdapter?.bluetoothLeScanner?.stopScan(this)
                onDeviceChosen(result.device)
            }
        }

        override fun onBatchScanResults(results: MutableList<ScanResult>) {
            super.onBatchScanResults(results)
            btAdapter?.bluetoothLeScanner?.stopScan(this)
            deviceSearchListener?.onDevicesFound(results.map { it.device })
            Timber.d("onBatchScanResults $results")
        }
    }

    init {
        Timber.d("init")
        if (btAdapter != null) {
            val filter = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
            filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED)
            filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED)
            filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)
            context.registerReceiver(btReceiver, filter)
        }
        if (PermissionUtil.hasPermissions(context, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                && isBtEnabled() && !isDeviceConnected()) {
            startSearch()
        }
        stateObservable.connect()
    }

    private fun onBtStateChanged(intent: Intent) {
        val state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                BluetoothAdapter.ERROR)
        when (state) {
            BluetoothAdapter.STATE_OFF -> {
                if (::btStateListener.isInitialized) {
                    btStateListener.invoke(false)
                }
            }
            BluetoothAdapter.STATE_TURNING_OFF -> {
            }
            BluetoothAdapter.STATE_ON -> {
                if (::btStateListener.isInitialized) {
                    btStateListener.invoke(true)
                }
            }
            BluetoothAdapter.STATE_TURNING_ON -> {
            }
        }
    }

    fun setBtStateListener(btStateListener: (Boolean) -> Unit) {
        this.btStateListener = btStateListener
    }

    fun setDeviceSearchListener(deviceSearchListener: DeviceSearchListener) {
        this.deviceSearchListener = deviceSearchListener
    }

    fun isBtEnabled() = btAdapter != null && btAdapter.isEnabled

    fun startSearch() {
        Timber.d("start search")
        killGatt()
        val settings = ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_BALANCED).build()
        val filters = listOf(ScanFilter.Builder().setServiceUuid(ParcelUuid.fromString(SERVICE_UUID)).build())
        //val filters = mutableListOf<ScanFilter>()//todo!
        btAdapter!!.bluetoothLeScanner.startScan(filters, settings, leScanCallback)
    }

    fun onDeviceChosen(device: BluetoothDevice) {
        Timber.d("device $device")
        Timber.d("gatt == null " + (gatt == null))
        if (gatt == null) {
            stateEmmiter.onNext(BT_CONNECTION_PROGRESS)
            gatt = device.connectGatt(context, true, gattCallback)
        }
    }

    private val gattCallback = object : BluetoothGattCallback() {

        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)
            Timber.d("onConnectionStateChange $gatt, status $status, state $newState")
            Timber.d("gatt $gatt")
            if (newState == BluetoothGatt.STATE_DISCONNECTED) {
                stateEmmiter.onNext(BT_CONNECTION_INACTIVE)
            }

            val result = gatt.discoverServices()
            Timber.d("discover services $result")
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            super.onServicesDiscovered(gatt, status)
            Timber.d("onServicesDiscovered status $status")
            Timber.d("bluetoothGatt services ${gatt.services}")
            if (status == BluetoothGatt.GATT_SUCCESS) {
                val service = gatt.getService(SERVICE_UUID.toUUID())
                Timber.d("service $service")
                Timber.d("service.characteristics ${service?.characteristics}")
                if (service == null || service.characteristics.isEmpty()) {
                    deviceSearchListener?.onError(context.getString(R.string.error_device_does_not_support_bluetooth))
                    return
                }
                deviceSearchListener?.onDevicePaired(gatt.device.name)
                val characteristic = service.characteristics[0]
                val result = setCharacteristicNotification(gatt, characteristic, true)
                Timber.d("result $result")
                stateEmmiter.onNext(BT_CONNECTION_ACTIVE)
                deviceApi = DeviceApi(gatt, characteristic)
                Timber.d("service.characteristics ${service.characteristics}")
            } else {
            }
        }

        override fun onCharacteristicRead(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, status: Int) {
            super.onCharacteristicRead(gatt, characteristic, status)
            Timber.d("onCharacteristicRead, status $status")
            deviceApi?.onCharacteristicWrite(status, characteristic.value?.let { String(it) })
        }

        @SuppressLint("BinaryOperationInTimber")
        override fun onCharacteristicWrite(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic, status: Int) {
            super.onCharacteristicWrite(gatt, characteristic, status)
            Timber.d("onCharacteristicWrite, status $status " + String(characteristic.value))
            gatt?.readCharacteristic(characteristic)
        }

        override fun onCharacteristicChanged(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic) {
            super.onCharacteristicChanged(gatt, characteristic)
            val chunk = String(characteristic.value)
            Timber.d("onCharacteristicChanged $chunk")
            deviceApi?.onCharacteristicChanged(chunk)
        }

        override fun onDescriptorRead(gatt: BluetoothGatt?, descriptor: BluetoothGattDescriptor?, status: Int) {
            super.onDescriptorRead(gatt, descriptor, status)
            Timber.d("onDescriptorRead")
        }

        override fun onDescriptorWrite(gatt: BluetoothGatt?, descriptor: BluetoothGattDescriptor?, status: Int) {
            super.onDescriptorWrite(gatt, descriptor, status)
            Timber.d("onDescriptorWrite")
        }

        override fun onMtuChanged(gatt: BluetoothGatt?, mtu: Int, status: Int) {
            super.onMtuChanged(gatt, mtu, status)
            Timber.d("onMtuChanged")
        }

        override fun onPhyRead(gatt: BluetoothGatt?, txPhy: Int, rxPhy: Int, status: Int) {
            super.onPhyRead(gatt, txPhy, rxPhy, status)
            Timber.d("onPhyRead")
        }

        override fun onPhyUpdate(gatt: BluetoothGatt?, txPhy: Int, rxPhy: Int, status: Int) {
            super.onPhyUpdate(gatt, txPhy, rxPhy, status)
            Timber.d("onPhyUpdate")
        }

        override fun onReadRemoteRssi(gatt: BluetoothGatt?, rssi: Int, status: Int) {
            super.onReadRemoteRssi(gatt, rssi, status)
            Timber.d("onReadRemoteRssi")
            deviceApi?.onReadRemoteRssi(gatt, rssi, status)
        }

        override fun onReliableWriteCompleted(gatt: BluetoothGatt?, status: Int) {
            super.onReliableWriteCompleted(gatt, status)
            Timber.d("onReliableWriteCompleted")
        }
    }

    fun setCharacteristicNotification(bluetoothGatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, enable: Boolean): Boolean {
        Timber.d("setCharacteristicNotification $enable")
        bluetoothGatt.setCharacteristicNotification(characteristic, enable)
        val descriptor = characteristic.getDescriptor(CHARACTERISTIC_UPDATE_NOTIFICATION_DESCRIPTOR_UUID)
        return if (descriptor != null) {
            descriptor.value = if (enable) BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE else byteArrayOf(0x00, 0x00)
            bluetoothGatt.writeDescriptor(descriptor)
        } else {
            false
        }
    }

    fun stopSearch() {
        Timber.d("stopSearch")
        btAdapter?.bluetoothLeScanner?.stopScan(leScanCallback)
        try {
            context.unregisterReceiver(btReceiver)
        } catch (ignored: Exception) {
        }
    }

    @SuppressLint("CheckResult")
    fun sendStart(progressCallback: (Progress) -> Unit) {
        deviceApi?.sendStart()
                ?.subscribe({progressCallback.invoke(Progress((it.progress * 100).toInt())) }, {})
    }

    @SuppressLint("CheckResult")
    fun sendStop(progressCallback: (Progress) -> Unit) {
        deviceApi?.sendStop()
                ?.subscribe({progressCallback.invoke(Progress((it.progress * 100).toInt())) }, {})
    }

    @SuppressLint("CheckResult")
    fun sendProgram(program: MyoProgram, progressCallback: (Progress) -> Unit) {
        deviceApi?.sendProgram(programMapper.mapToProgramCommand(program))
                ?.subscribe({progressCallback.invoke(Progress((it.progress * 100).toInt())) }, {})
    }

    @SuppressLint("CheckResult")
    fun sync(progressCallback: (Progress) -> Unit) {
        deviceApi?.syncTime()
                ?.subscribe({progressCallback.invoke(Progress((it.progress * 100).toInt())) }, {})
    }

    fun isDeviceActive(): Boolean {
        return this.getDevice() != null
    }

    private fun getDevice(): BluetoothDevice? {
        val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val devices = bluetoothManager.getConnectedDevices(BluetoothProfile.GATT)
        return if (devices.isNotEmpty()) devices[0] else null
    }

    fun isDeviceConnected() = gatt != null

    fun unbindDevice() {
        prefsRepository.deviceName = ""
        stateEmmiter.onNext(BT_CONNECTION_INACTIVE)
        if (removeDevice()) return
        val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val devices = bluetoothManager.getConnectedDevices(BluetoothProfile.GATT)
        if (devices.isNotEmpty()) {
            devices[0].connectGatt(context, false, object : BluetoothGattCallback() {
                override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
                    super.onConnectionStateChange(gatt, status, newState)
                    this@BtInteractor.gatt = gatt
                    Timber.d("onConnectionStateChange $gatt, status $status, newState $newState")
                    removeDevice()
                }
            })
        } else {
            if (::unbindListener.isInitialized) unbindListener.invoke(true)
        }
    }

    fun removeDevice(): Boolean {
        gatt?.apply {
            killGatt()
            prefsRepository.deviceName = ""
            if (::unbindListener.isInitialized) unbindListener.invoke(true)
            return true
        }
        return false
    }

    private fun killGatt() {
        Timber.d("killGatt")
        gatt?.disconnect()
        gatt?.close()
        gatt = null
        deviceApi?.onDeviceDisconnected()
    }

    fun setUnbindListener(unbindListener: (Boolean) -> Unit) {
        this.unbindListener = unbindListener
    }

    companion object {
        val BT_CONNECTION_INACTIVE = 0
        val BT_CONNECTION_ACTIVE = 1
        val BT_CONNECTION_PROGRESS = 2
        private const val SERVICE_UUID = "0000ffe0-0000-1000-8000-00805f9b34fb"
        //private val CHARACTERISTIC_UPDATE_NOTIFICATION_DESCRIPTOR_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")
        private val CHARACTERISTIC_UPDATE_NOTIFICATION_DESCRIPTOR_UUID = UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb")
    }

    enum class BtPower {
        WEAK,
        MID,
        STRONG;
        companion object {
            fun getPower(rssi: Int): BtPower {
                if (abs(rssi).toDouble() < 8.5 * 8.5) return STRONG
                if (abs(rssi).toDouble() < 9.2 * 9.2) return MID
                return WEAK
            }
        }
    }
}