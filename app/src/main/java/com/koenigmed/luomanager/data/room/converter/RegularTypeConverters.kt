package com.koenigmed.luomanager.data.room.converter

import android.arch.persistence.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.koenigmed.luomanager.data.server.deserializer.DateTimeTypeAdapter.Companion.TIME_ZONE_OFFSET_SEC
import com.koenigmed.luomanager.data.server.deserializer.TimeTypeAdapter.Companion.DATE_PATTERN
import com.koenigmed.luomanager.util.GsonUtil
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.LocalTime
import org.threeten.bp.ZoneOffset
import org.threeten.bp.format.DateTimeFormatterBuilder
import java.util.*

class RegularTypeConverters {
    var gson = Gson()

    @TypeConverter
    fun dateToLong(date: LocalDate?): Long? {
        if (date == null) {
            return null
        }
        return date.toEpochDay()
    }

    @TypeConverter
    fun longToDate(date: Long?): LocalDate? {
        if (date == null) {
            return null
        }
        return LocalDate.ofEpochDay(date)
    }

    @TypeConverter
    fun dateTimeToLong(dateTime: LocalDateTime?): Long? {
        if (dateTime == null) {
            return null
        }
        return dateTime.toEpochSecond(getZoneOffset())
    }

    @TypeConverter
    fun longToDateTime(dateTime: Long?): LocalDateTime? {
        if (dateTime == null) {
            return null
        }
        return LocalDateTime.ofEpochSecond(dateTime, 0, getZoneOffset())
    }

    @TypeConverter
    fun timeToLong(time: LocalTime?): Long? {
        if (time == null) {
            return null
        }
        return time.toNanoOfDay()
    }

    @TypeConverter
    fun longToTime(time: Long?): LocalTime? {
        if (time == null) {
            return null
        }
        return LocalTime.ofNanoOfDay(time)
    }

    @TypeConverter
    fun timeListToString(time: List<LocalTime>?): String? {
        if (time == null) {
            return null
        }
        return GsonUtil.gson().toJson(time)
    }

    @TypeConverter
    fun stringToTimeList(time: String?): List<LocalTime>? {
        if (time == null) {
            return null
        }
        val listType = object : TypeToken<List<String>>() {}.type
        val fromJson = GsonUtil.gson().fromJson<List<String>?>(time, listType)
        val fmt = DateTimeFormatterBuilder()
                .appendPattern(DATE_PATTERN)
                .toFormatter(Locale.US)
        return fromJson!!.map { LocalTime.parse(it, fmt) }
    }

    private fun getZoneOffset() = ZoneOffset.ofTotalSeconds(TIME_ZONE_OFFSET_SEC.toInt())
}