package com.rita.calendarprooo

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.rita.calendarprooo.data.Check
import com.rita.calendarprooo.data.Plan

@BindingAdapter("title")
fun TextView.bindTitle(item: Plan) {
    item?.let{
        text="${it.title}"
    }
}

@BindingAdapter("description")
fun TextView.bindDescription(item: Plan) {
    item?.let{
        text="${it.description}"
    }
}

@BindingAdapter("location")
fun TextView.bindLocation(item: Plan) {
    item?.let{
        text="${it.title}"
    }
}

@BindingAdapter("checkTitle")
fun TextView.bindCheckTitle(item: Check) {
    item?.let{
        text="${it.title}"
    }
}