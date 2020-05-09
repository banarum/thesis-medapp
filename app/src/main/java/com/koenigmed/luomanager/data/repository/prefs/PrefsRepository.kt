package com.koenigmed.luomanager.data.repository.prefs

import android.content.Context
import com.f2prateek.rx.preferences2.RxSharedPreferences
import org.threeten.bp.Instant
import javax.inject.Inject

class PrefsRepository
@Inject constructor(private val context: Context) : IPrefsRepository {

    private var authPrefs: RxSharedPreferences
    private var userPrefs: RxSharedPreferences

    init {
        authPrefs = RxSharedPreferences.create(context.getSharedPreferences(AUTH_DATA, Context.MODE_PRIVATE))
        userPrefs = RxSharedPreferences.create(context.getSharedPreferences(USER_DATA, Context.MODE_PRIVATE))
    }

    companion object {
        private const val AUTH_DATA = "auth_data"
        private const val USER_DATA = "user_data"
        private const val KEY_TOKEN = "auth_token"
        private const val KEY_DEVICE_NAME = "device"
        private const val KEY_PUSHES_ENABLED = "pushes"
        private const val KEY_FEELS_ANSWERED_DATE = "KEY_FEELS_ANSWERED_DATE"
        private const val KEY_PAIN_LEVEL_ANSWERED_DATE = "KEY_PAIN_LEVEL_ANSWERED_DATE"
        private const val KEY_SYNC_DATE = "KEY_SYNC_DATE"
    }

    override var token: String
        get() = authPrefs.getString(KEY_TOKEN, "").get()
        set(value) {
            authPrefs.getString(KEY_TOKEN).set(value)
        }

    override fun logout() {
        authPrefs.clear()
        userPrefs.clear()
    }

    override var deviceName: String
        get() = userPrefs.getString(KEY_DEVICE_NAME, "").get()
        set(value) {
            userPrefs.getString(KEY_DEVICE_NAME).set(value)
        }

    override var pushesEnabled: Boolean
        get() = userPrefs.getBoolean(KEY_PUSHES_ENABLED, true).get()
        set(value) {
            userPrefs.getBoolean(KEY_PUSHES_ENABLED).set(value)
        }

    override var feelsAnsweredDate: Long
        get() = userPrefs.getLong(KEY_FEELS_ANSWERED_DATE, Instant.MIN.epochSecond).get()
        set(value) {
            userPrefs.getLong(KEY_FEELS_ANSWERED_DATE).set(value)
        }

    override var painLevelAnsweredDate: Long
        get() = userPrefs.getLong(KEY_PAIN_LEVEL_ANSWERED_DATE, Instant.MIN.epochSecond).get()
        set(value) {
            userPrefs.getLong(KEY_PAIN_LEVEL_ANSWERED_DATE).set(value)
        }

    override var syncDate: Long
        get() = userPrefs.getLong(KEY_SYNC_DATE, Instant.MIN.epochSecond).get()
        set(value) {
            userPrefs.getLong(KEY_SYNC_DATE).set(value)
        }
}