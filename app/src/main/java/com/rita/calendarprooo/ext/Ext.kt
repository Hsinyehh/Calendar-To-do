package com.rita.calendarprooo.ext


import androidx.core.content.ContextCompat
import com.rita.calendarprooo.CalendarProApplication
import com.rita.calendarprooo.Util.Logger
import java.text.SimpleDateFormat
import java.util.*

fun getToday(): String {
    val df = SimpleDateFormat("dd-MM-yyyy")
    val today = df.format(Calendar.getInstance().getTime())
    Logger.i("getToday() - $today")
    return today
}

fun convertToTimeStamp(dateSelected: String): List<Long>? {
    try {
        val startTime = "$dateSelected 00:00"
        val endTime = "$dateSelected 23:59"

        val startDateSelectedFormat = SimpleDateFormat("dd-MM-yyyy HH:mm").parse(startTime)
        val endDateSelectedFormat = SimpleDateFormat("dd-MM-yyyy HH:mm").parse(endTime)
        Logger.i("convertToTimeStamp: ${startDateSelectedFormat.time} ")

        val selectedStartTime = startDateSelectedFormat.time
        val selectedEndTime = endDateSelectedFormat.time
        var list = listOf<Long>(selectedStartTime, selectedEndTime)

        return list

    } catch (e: java.text.ParseException) {
        Logger.i("$e")

        return null
    }
}

fun getColorCode(color: Int): Int {
    return ContextCompat.getColor(CalendarProApplication.appContext!!, color)
}

fun stringToTimestamp(dateSelected: String): Long? {
    try {
        val dateSelectedFormat = SimpleDateFormat("dd-MM-yyyy HH:mm").parse(dateSelected)
        Logger.i("stringToTimestamp: ${dateSelectedFormat.time} ")

        return dateSelectedFormat.time

    } catch (e: java.text.ParseException) {
        Logger.i("stringToTimestamp: $e")
        return null
    }
}

fun timestampToString(timestamp: Long): String {
    val simpleDateFormat = SimpleDateFormat("MM/dd HH:mm", Locale.TAIWAN)

    return simpleDateFormat.format(Date(timestamp))
}
