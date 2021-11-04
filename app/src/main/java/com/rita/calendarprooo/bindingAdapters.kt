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

@BindingAdapter("owner_name")
fun TextView.bindOwnerName(item: Plan?) {
    item?.let{
        text="${it.owner_name}"
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


@BindingAdapter("done_info")
fun TextView.bindDoneInfo(item: Plan?) {
    val simpleDateFormat = SimpleDateFormat("MM/dd hh:mm",  Locale.TAIWAN)
    item?.let{ it ->
        var doneTime : String?  = null
        it.done_time?.let{
            doneTime = simpleDateFormat.format( Date(it) )
        }
        text = if(item.doner!=null){
            "$doneTime By ${item.doner}"
        }else{
            ""
        }
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
    item?.name?.let{
        text="$it"
    }
}

@BindingAdapter("checkDoneTime")
fun TextView.bindDoneTime(item: Check?) {
    val simpleDateFormat = SimpleDateFormat("MM/dd hh:mm",  Locale.TAIWAN)
    var doneTime : String?  = null
    item?.done_time?.let{
        doneTime = simpleDateFormat.format( Date(it) )
        text = "$doneTime"?:""
    }

}

@BindingAdapter("checkDone")
fun TextView.bindDone(item: Check?) {
    item?.doner?.let{
        text="By $it"
    }
}


/*@BindingAdapter("detailView")
fun bindDetailView(layout:ConstraintLayout, item:  Long) {

    Log.i("Rita","bindDetailView- $item")
}*/