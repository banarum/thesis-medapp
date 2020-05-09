package com.koenigmed.luomanager.data.server.deserializer

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import com.koenigmed.luomanager.data.server.deserializer.DateTimeTypeAdapter.Companion.TIME_ZONE_OFFSET_SEC
import org.threeten.bp.LocalDate
import org.threeten.bp.ZonedDateTime

class DateTypeAdapter : TypeAdapter<LocalDate>() {

    override fun write(out: JsonWriter, value: LocalDate) {
        out.value(listOf(value.year, value.monthValue, value.dayOfMonth).joinToString("-"))
    }

    // didn't work for null birthdate, why - xz
    override fun read(reader: JsonReader): LocalDate? {
        val token = reader.peek()
        return if (token == JsonToken.STRING) {
            ZonedDateTime.parse(reader.nextString()).plusSeconds(TIME_ZONE_OFFSET_SEC).toLocalDate()
        } else {
            LocalDate.now()
        }
    }
}