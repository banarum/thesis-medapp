package com.koenigmed.luomanager.domain.interactor.device

import com.koenigmed.luomanager.data.repository.prefs.IPrefsRepository
import com.koenigmed.luomanager.system.SchedulersProvider
import org.threeten.bp.Duration
import org.threeten.bp.Instant
import javax.inject.Inject

class DeviceInteractor
@Inject constructor(private val prefsRepository: IPrefsRepository,
                    private val schedulers: SchedulersProvider) {

    fun isDeviceConnected() = prefsRepository.deviceName.isNotEmpty()

    fun getDeviceName() = prefsRepository.deviceName

    fun setDeviceName(deviceName: String) {
        prefsRepository.deviceName = deviceName
    }

    fun needSync() = Duration.between(Instant.ofEpochSecond(prefsRepository.syncDate), Instant.now()) > Duration.ofDays(1)

    fun setSynced() {
        prefsRepository.syncDate = Instant.now().epochSecond
    }
}