package com.rita.calendarprooo.ext

import android.app.Activity
import android.graphics.Color
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import com.rita.calendarprooo.CalendarProApplication
import com.rita.calendarprooo.R
import java.text.SimpleDateFormat
import java.util.*

fun getToday() : String {
    val df = SimpleDateFormat("dd-MM-yyyy");
    val today = df.format(Calendar.getInstance().getTime())
    Log.i("Rita","getToday() - $today")
    return today
}

fun convertToTimeStamp(dateSelected:String) : List<Long>? {
    try {
        var selectedStartTime:Long = 0L
        var selectedEndTime:Long = 0L

        val startTime= "$dateSelected 00:00"
        val endTime= "$dateSelected 23:59"

        val startDateSelectedFormat = SimpleDateFormat("dd-MM-yyyy HH:mm").parse(startTime)
        val endDateSelectedFormat = SimpleDateFormat("dd-MM-yyyy HH:mm").parse(endTime)
        Log.i("Rita", "convertToTimeStamp: ${startDateSelectedFormat.time} ")

        selectedStartTime = startDateSelectedFormat.time
        selectedEndTime = endDateSelectedFormat.time
        var list = listOf<Long>(selectedStartTime,selectedEndTime)

        return list
    }
    catch(e:java.text.ParseException){
        Log.i("Rita","$e")
        return null
    }
}

fun getColorCode(color: Int) : Int{
    return ContextCompat.getColor(CalendarProApplication.appContext!!, color)
}

