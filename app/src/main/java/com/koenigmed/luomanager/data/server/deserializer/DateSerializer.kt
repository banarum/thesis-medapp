package com.koenigmed.luomanager.data.server.deserializer

import com.google.gson.JsonElement
import com.google.gson.JsonParser
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import org.threeten.bp.LocalDate
import java.lang.reflect.Type

class DateSerializer : JsonSerializer<LocalDate> {

    override fun serialize(value: LocalDate, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        val s = listOf(value.year, value.month, value.dayOfMonth).joinToString("-")
        return JsonParser().parse(s).asJsonObject
    }

}