package com.koenigmed.luomanager.data.room.converter

import android.arch.persistence.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.koenigmed.luomanager.domain.model.program.MyoProgramMyoTask
import java.util.*

class MyoTypeConverters {
    var gson = Gson()

    @TypeConverter
    fun stringToTaskList(data: String?): List<MyoProgramMyoTask> {
        if (data == null) {
            return Collections.emptyList()
        }

        val listType = object : TypeToken<List<MyoProgramMyoTask>>() {
        }.type

        return gson.fromJson<List<MyoProgramMyoTask>>(data, listType)
    }

    @TypeConverter
    fun taskListToString(someObjects: List<MyoProgramMyoTask>): String {
        return gson.toJson(someObjects)
    }
}