package com.rita.calendarprooo

import android.util.Log
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BindingAdapter
import com.rita.calendarprooo.data.Category
import com.rita.calendarprooo.data.Check
import com.rita.calendarprooo.data.Plan
import java.text.SimpleDateFormat
import java.util.*

@BindingAdapter("title")
fun TextView.bindTitle(item: Plan?) {
    item?.let{
        text="${it.title}"
    }
}

@BindingAdapter("description")
fun TextView.bindDescription(item: Plan?) {
    item?.let{
        text="${it.description}"
    }
}

@BindingAdapter("location")
fun TextView.bindLocation(item: Plan?) {
    item?.let{
        text="${it.location}"
    }
}

@BindingAdapter("owner")
fun TextView.bindOwner(item: Plan?) {
    item?.let{
        text="${it.owner}"
    }
}

@BindingAdapter("time")
fun TextView.bindTime(item: Plan?) {
    val simpleDateFormat = SimpleDateFormat("MM/dd hh:mm",  Locale.TAIWAN)
    item?.let{
        val startTime=simpleDateFormat.format(it.start_time?.let { it1 -> Date(it1) })
        val endTime=simpleDateFormat.format(it.end_time?.let { it1 -> Date(it1) })

        text="$startTime  -  $endTime"
    }
}

@BindingAdapter("checkTitle")
fun TextView.bindCheckTitle(item: Check?) {
    item?.let{
        text="${item.title}"
    }
    if(item==null){
        text=""
    }
}

@BindingAdapter("categoryName")
fun TextView.bindCategoryTitle(item: Category?) {
    item?.let{
        text="${it.name}"
    }
}


/*@BindingAdapter("detailView")
fun bindDetailView(layout:ConstraintLayout, item:  Long) {

    Log.i("Rita","bindDetailView- $item")
}*/