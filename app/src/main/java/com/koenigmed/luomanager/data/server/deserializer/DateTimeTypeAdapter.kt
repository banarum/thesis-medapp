package com.koenigmed.luomanager.data.server.deserializer

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZonedDateTime
import java.util.*
import java.util.concurrent.TimeUnit

class DateTimeTypeAdapter : TypeAdapter<LocalDateTime>() {

    override fun write(out: JsonWriter, value: LocalDateTime) {
        out.value(value.toString())
    }

    override fun read(reader: JsonReader): LocalDateTime? {
        val token = reader.peek()
        return if (token == JsonToken.STRING) {
            ZonedDateTime.parse(reader.nextString()).plusSeconds(TIME_ZONE_OFFSET_SEC).toLocalDateTime()
        } else {
            null
        }
    }

    companion object {
        val TIME_ZONE_OFFSET_SEC = TimeUnit.MILLISECONDS.toSeconds(TimeZone.getDefault().rawOffset.toLong())
    }
}