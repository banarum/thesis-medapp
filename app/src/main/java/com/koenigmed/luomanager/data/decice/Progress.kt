package com.koenigmed.luomanager.data.decice

import android.support.annotation.StringRes
import com.google.gson.JsonSyntaxException
import com.koenigmed.luomanager.R
import com.koenigmed.luomanager.util.GsonUtil

data class Progress(val progress: Int, val response: String? = null) {

    fun isComplete() = progress == 100

    fun isError() = progress < 0

    @StringRes
    fun getErrorMessageId():  Int {
        val response = try {
            response?.let { GsonUtil.gson().fromJson(it, DeviceResponse::class.java) }
        } catch (ex: JsonSyntaxException) {
        }
        return R.string.error_device
    }
}