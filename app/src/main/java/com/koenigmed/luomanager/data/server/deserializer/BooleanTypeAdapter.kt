package com.koenigmed.luomanager.data.server.deserializer

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter

class BooleanTypeAdapter : TypeAdapter<Boolean>() {

    override fun read(inReader: JsonReader): Boolean {
        return inReader.nextBoolean()
    }

    override fun write(out: JsonWriter, value: Boolean) {
        out.value("$value")
    }

    companion object {
    }
}