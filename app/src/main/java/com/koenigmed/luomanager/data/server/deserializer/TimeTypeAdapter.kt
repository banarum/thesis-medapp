package com.koenigmed.luomanager.data.server.deserializer

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import org.threeten.bp.LocalTime
import org.threeten.bp.format.DateTimeFormatterBuilder
import java.util.*

class TimeTypeAdapter : TypeAdapter<LocalTime>() {

    override fun read(reader: JsonReader): LocalTime {
        val fmt = DateTimeFormatterBuilder()
                .appendPattern(DATE_PATTERN)
                .toFormatter(Locale.US)
        return LocalTime.parse(reader.nextString(), fmt)
    }

    override fun write(out: JsonWriter, value: LocalTime) {
        val fmt = DateTimeFormatterBuilder()
                .appendPattern(DATE_PATTERN)
                .toFormatter(Locale.US)
        out.value(fmt.format(value))
    }

    companion object {
        const val DATE_PATTERN = "HH:mm:ss"
    }
}