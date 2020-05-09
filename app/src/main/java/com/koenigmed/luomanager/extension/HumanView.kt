package com.koenigmed.luomanager.extension

import android.content.Context
import com.koenigmed.luomanager.R
import com.koenigmed.luomanager.presentation.mvp.receipt.DownloadStatus
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime
import org.threeten.bp.Period
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter
import java.util.*


fun DownloadStatus.getIcon() = when (this) {
    DownloadStatus.NOT_DOWNLOADED -> R.drawable.ic_download
    DownloadStatus.IN_PROGRESS -> R.drawable.ic_download
    DownloadStatus.COMPLETE -> R.drawable.ic_download_complete
}

fun LocalDate.getAge() = Period.between(this, ZonedDateTime.now().toLocalDate()).years.toString()

fun LocalDate.getHistoryDate(context: Context): String {
    val now = LocalDate.now()
    return when {
        isSameDay(now) -> context.getString(R.string.today)
        isSameDay(now.minusDays(1)) -> context.getString(R.string.yesterday)
        else -> {
            val format = DateTimeFormatter.ofPattern("dd LLL yyyy", Locale.getDefault())
            format.format(this)
        }
    }
}

fun LocalDate.isSameDay(now: LocalDate) =
        this.year == now.year && this.month == now.month && this.dayOfMonth == now.dayOfMonth

fun LocalTime.getTimeString(): String {
    var hr = hour.toString()
    if (hour < 10) {
        hr = "0$hour"
    }
    var mn = minute.toString()
    if (minute < 10) {
        mn = "0$minute"
    }
    return "$hr:$mn"
}

fun LocalTime.getTimerString(): String {
    var hr = ""
    if (hour > 0) {
        hr = "$hour:"
    }
    var sec = "$second"
    if (second < 10) {
        sec = "0$second"
    }
    return "$hr$minute:$sec"
}

fun Long.getTimeStringFromSeconds(context: Context): String {
    return when {
        this < 60 -> context.getString(R.string.time_sec_format, this)
        this < 60 * 60 -> {
            val result = context.getString(R.string.time_min_format, this / 60)
            val rem = this.rem(60)
            if (rem != 0L) result + " " + rem.getTimeStringFromSeconds(context) else result
        }
        else -> {
            val result = context.getString(R.string.time_hour_format, this / (60 * 60))
            val rem = this.rem(60 * 60)
            if (rem != 0L) result + " " + rem.getTimeStringFromSeconds(context) else result

        }
    }
}