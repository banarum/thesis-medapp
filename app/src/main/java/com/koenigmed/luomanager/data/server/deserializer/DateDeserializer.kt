package com.koenigmed.luomanager.data.server.deserializer

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatterBuilder
import java.lang.reflect.Type
import java.util.*

class DateDeserializer : JsonDeserializer<LocalDate> {

    override fun deserialize(
            json: JsonElement,
            type: Type,
            jsonDeserializationContext: JsonDeserializationContext
    ): LocalDate {

        val fmt = DateTimeFormatterBuilder()
                .appendPattern(DATE_PATTERN)
                .toFormatter(Locale.US)
        return LocalDate.parse(json.asJsonPrimitive.asString, fmt)
    }

    companion object {
        const val DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS"
    }
}