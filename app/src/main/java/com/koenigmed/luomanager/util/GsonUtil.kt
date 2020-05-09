package com.koenigmed.luomanager.util

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.koenigmed.luomanager.data.server.deserializer.TimeTypeAdapter
import org.threeten.bp.LocalTime

object GsonUtil {

    private var gson: Gson? = null

    fun gson(): Gson {
        if (gson == null) {
            gson = GsonBuilder()
                    .registerTypeAdapter(LocalTime::class.java, TimeTypeAdapter())
                    .create()
        }
        return gson!!
    }
}