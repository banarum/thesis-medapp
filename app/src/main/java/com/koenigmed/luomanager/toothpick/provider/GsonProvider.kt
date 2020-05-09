package com.koenigmed.luomanager.toothpick.provider

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.koenigmed.luomanager.data.server.deserializer.DateDeserializer
import com.koenigmed.luomanager.data.server.deserializer.DateTypeAdapter
import com.koenigmed.luomanager.data.server.deserializer.TimeTypeAdapter
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime
import javax.inject.Inject
import javax.inject.Provider

class GsonProvider @Inject constructor() : Provider<Gson> {

    override fun get(): Gson =
            GsonBuilder()
                    .registerTypeAdapter(LocalDate::class.java, DateTypeAdapter())
                    // cause nulls come from json, it handles them correctly
                    .registerTypeAdapter(LocalDate::class.java, DateDeserializer())
                    .registerTypeAdapter(LocalTime::class.java, TimeTypeAdapter())
                    .serializeNulls()
                    .create()
}