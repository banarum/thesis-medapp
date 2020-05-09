package com.koenigmed.luomanager.data.repository.prefs

interface IPrefsRepository {
    fun logout()

    var token: String
    var pushesEnabled: Boolean
    var deviceName: String
    var feelsAnsweredDate: Long
    var painLevelAnsweredDate: Long
    var syncDate: Long
}